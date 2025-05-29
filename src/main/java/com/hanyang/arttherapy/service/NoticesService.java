package com.hanyang.arttherapy.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanyang.arttherapy.common.exception.CustomException;
import com.hanyang.arttherapy.common.exception.exceptionType.FileSystemExceptionType;
import com.hanyang.arttherapy.common.exception.exceptionType.NoticeException;
import com.hanyang.arttherapy.common.filter.CustomUserDetail;
import com.hanyang.arttherapy.domain.Files;
import com.hanyang.arttherapy.domain.NoticeFiles;
import com.hanyang.arttherapy.domain.Notices;
import com.hanyang.arttherapy.domain.Users;
import com.hanyang.arttherapy.domain.enums.Role;
import com.hanyang.arttherapy.dto.request.NoticeRequestDto;
import com.hanyang.arttherapy.dto.response.FileResponseDto;
import com.hanyang.arttherapy.dto.response.noticeResponse.*;
import com.hanyang.arttherapy.dto.response.userResponse.CommonMessageResponse;
import com.hanyang.arttherapy.repository.FilesRepository;
import com.hanyang.arttherapy.repository.NoticeFilesRepository;
import com.hanyang.arttherapy.repository.NoticesRepository;
import com.hanyang.arttherapy.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NoticesService {
  private final NoticesRepository noticesRepository;
  private final NoticeFilesRepository noticeFilesRepository;
  private final UserRepository userRepository;
  private final FilesRepository filesRepository;

  // 게시판 전체 조회 (keyword가 없으면 전체 조회)
  public NoticeListResponseDto getNotices(String keyword, int page) {
    String trimmedKeyword = keyword != null ? keyword.trim() : null;
    Pageable pageable =
        PageRequest.of(page, 10, Sort.by(Sort.Order.desc("isFixed"), Sort.Order.desc("createdAt")));

    Page<Notices> noticesPage =
        (trimmedKeyword != null && !trimmedKeyword.isBlank())
            ? noticesRepository.findAllByKeyword(trimmedKeyword, pageable)
            : noticesRepository.findAll(pageable);

    List<NoticeResponseDto> content =
        noticesPage.getContent().stream()
            .map(
                notice ->
                    new NoticeResponseDto(
                        notice.getNoticesNo(),
                        notice.getCategory().name(),
                        notice.getTitle(),
                        noticeFilesRepository.existsByNotice(notice),
                        notice.getViewCount(),
                        notice.getCreatedAt(),
                        notice.isFixed()))
            .toList();

    return new NoticeListResponseDto(
        content,
        noticesPage.getNumber(),
        noticesPage.getSize(),
        (int) noticesPage.getTotalElements(),
        noticesPage.getTotalPages(),
        noticesPage.isLast());
  }

  // 게시글 상세 조회 + 조회수 증가 + 파일 + 이전글,다음글
  public NoticeDetailResponseDto getNoticeDetail(Long noticeNo) {
    Notices notice = findNoticeOrThrow(noticeNo);
    notice.increaseViewCount();

    List<FileResponseDto> files = getFileResponses(notice);
    AdjacentNoticeDto previous = getPreviousNotice(notice.getCreatedAt());
    AdjacentNoticeDto next = getNextNotice(notice.getCreatedAt());

    return buildDetailResponse(notice, files, previous, next);
  }

  // 게시글 등록
  public CommonDataResponse<NoticeDetailResponseDto> createNotice(NoticeRequestDto dto) {
    validateNoticeRequest(dto);
    Users currentUser = getAdminUserOrThrow();

    Notices notice =
        Notices.builder()
            .title(dto.title())
            .category(dto.category())
            .periodStart(dto.periodStart())
            .periodEnd(dto.periodEnd())
            .content(dto.content())
            .user(currentUser)
            .isFixed(Boolean.TRUE.equals(dto.isFixed()))
            .build();

    noticesRepository.save(notice);

    List<FileResponseDto> files = saveAndMapFiles(dto.filesNo(), notice);
    AdjacentNoticeDto previous = getPreviousNotice(notice.getCreatedAt());
    AdjacentNoticeDto next = getNextNotice(notice.getCreatedAt());

    NoticeDetailResponseDto response = buildDetailResponse(notice, files, previous, next);
    return new CommonDataResponse<>("게시글 등록이 완료되었습니다.", response);
  }

  // 게시글 수정
  public CommonDataResponse<NoticeDetailResponseDto> updateNotice(
      Long noticeNo, NoticeRequestDto dto) {
    Users currentUser = getAdminUserOrThrow();
    Notices notice = findNoticeOrThrow(noticeNo);

    notice.update(
        dto.title(),
        dto.content(),
        dto.category(),
        dto.periodStart(),
        dto.periodEnd(),
        Boolean.TRUE.equals(dto.isFixed()));

    deleteOldFiles(notice);
    List<FileResponseDto> files = saveAndMapFiles(dto.filesNo(), notice);
    AdjacentNoticeDto previous = getPreviousNotice(notice.getCreatedAt());
    AdjacentNoticeDto next = getNextNotice(notice.getCreatedAt());

    NoticeDetailResponseDto response = buildDetailResponse(notice, files, previous, next);
    return new CommonDataResponse<>("게시글 수정이 완료되었습니다.", response);
  }

  // 게시글 삭제
  public CommonMessageResponse deleteNotice(Long noticeNo) {
    Users currentUser = getAdminUserOrThrow();
    Notices notice = findNoticeOrThrow(noticeNo);

    deleteOldFiles(notice);
    noticesRepository.delete(notice);

    return new CommonMessageResponse("게시글 삭제가 완료되었습니다.");
  }

  private Notices findNoticeOrThrow(Long noticeNo) {
    return noticesRepository
        .findByNoticesNo(noticeNo)
        .orElseThrow(() -> new CustomException(NoticeException.NOTICE_NOT_FOUND));
  }

  // 관리자 권한 확인
  private Users getAdminUserOrThrow() {
    Users user = getCurrentUser();
    if (!user.getRole().equals(Role.ADMIN)) {
      throw new CustomException(NoticeException.NOT_ADMIN);
    }
    return user;
  }

  private Users getCurrentUser() {
    var auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
      throw new CustomException(NoticeException.UNAUTHENTICATED);
    }

    Object principal = auth.getPrincipal();
    if (principal instanceof CustomUserDetail customUserDetail) {
      return customUserDetail.getUser();
    }

    throw new CustomException(NoticeException.UNAUTHENTICATED);
  }

  // 등록/수정 필수값 검증
  private void validateNoticeRequest(NoticeRequestDto dto) {
    if (dto.title() == null
        || dto.title().isBlank()
        || dto.content() == null
        || dto.content().isBlank()
        || dto.category() == null) {
      throw new CustomException(NoticeException.NOTICE_REQUIRED_FIELD_MISSING);
    }
  }

  // 파일 저장
  private List<FileResponseDto> saveAndMapFiles(List<Long> filesNo, Notices notice) {
    if (filesNo == null) return List.of();
    return filesNo.stream()
        .map(
            fileNo -> {
              Files file =
                  filesRepository
                      .findById(fileNo)
                      .orElseThrow(
                          () -> new CustomException(FileSystemExceptionType.FILE_NOT_FOUND));
              file.activateFile();
              filesRepository.save(file);
              noticeFilesRepository.save(NoticeFiles.builder().notice(notice).file(file).build());
              return FileResponseDto.of(file, file.getUrl());
            })
        .toList();
  }

  private void deleteOldFiles(Notices notice) {
    List<NoticeFiles> mappings = noticeFilesRepository.findAllByNotice(notice);
    mappings.forEach(
        mapping -> {
          Files file = mapping.getFile();
          file.markAsDeleted();
          filesRepository.save(file);
        });
    noticeFilesRepository.deleteAll(mappings);
  }

  private List<FileResponseDto> getFileResponses(Notices notice) {
    return noticeFilesRepository.findAllByNotice(notice).stream()
        .map(nf -> FileResponseDto.of(nf.getFile(), nf.getFile().getUrl()))
        .toList();
  }

  // 이전글 조회
  private AdjacentNoticeDto getPreviousNotice(LocalDateTime createdAt) {
    Page<Notices> page = noticesRepository.findPreviousNotice(createdAt, PageRequest.of(0, 1));
    return page.isEmpty() ? null : mapToAdjacent(page.getContent().get(0));
  }

  // 다음글 조회
  private AdjacentNoticeDto getNextNotice(LocalDateTime createdAt) {
    Page<Notices> page = noticesRepository.findNextNotice(createdAt, PageRequest.of(0, 1));
    return page.isEmpty() ? null : mapToAdjacent(page.getContent().get(0));
  }

  private NoticeDetailResponseDto buildDetailResponse(
      Notices notice, List<FileResponseDto> files, AdjacentNoticeDto prev, AdjacentNoticeDto next) {
    return new NoticeDetailResponseDto(
        notice.getNoticesNo(),
        notice.getTitle(),
        notice.getCategory().name(),
        notice.getCreatedAt(),
        notice.getPeriodStart(),
        notice.getPeriodEnd(),
        notice.getViewCount(),
        notice.getContent(),
        files,
        prev,
        next,
        notice.isFixed());
  }

  private AdjacentNoticeDto mapToAdjacent(Notices n) {
    return new AdjacentNoticeDto(n.getNoticesNo(), n.getTitle(), n.getCreatedAt().toString());
  }
}

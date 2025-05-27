package com.hanyang.arttherapy.service;

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
import com.hanyang.arttherapy.common.exception.exceptionType.UserException;
import com.hanyang.arttherapy.domain.Files;
import com.hanyang.arttherapy.domain.NoticeFiles;
import com.hanyang.arttherapy.domain.Notices;
import com.hanyang.arttherapy.domain.Users;
import com.hanyang.arttherapy.domain.enums.NoticeCategory;
import com.hanyang.arttherapy.domain.enums.Role;
import com.hanyang.arttherapy.dto.request.NoticeRequestDto;
import com.hanyang.arttherapy.dto.response.FileResponseDto;
import com.hanyang.arttherapy.dto.response.noticeResponse.AdjacentNoticeDto;
import com.hanyang.arttherapy.dto.response.noticeResponse.NoticeDetailResponseDto;
import com.hanyang.arttherapy.dto.response.noticeResponse.NoticeListResponseDto;
import com.hanyang.arttherapy.dto.response.noticeResponse.NoticeResponseDto;
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

  // 전체 조회
  public NoticeListResponseDto getNotices(String keyword, NoticeCategory category, int page) {
    Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

    Page<Notices> noticesPage = noticesRepository.findAllBySearch(category, keyword, pageable);

    List<NoticeResponseDto> content =
        noticesPage.getContent().stream()
            .map(
                notice -> {
                  boolean hasFile = noticeFilesRepository.existsByNotice(notice);
                  return new NoticeResponseDto(
                      notice.getNoticesNo(),
                      notice.getCategory().name(),
                      notice.getTitle(),
                      hasFile,
                      notice.getViewCount(),
                      notice.getCreatedAt());
                })
            .toList();

    return new NoticeListResponseDto(
        content,
        noticesPage.getNumber(),
        noticesPage.getSize(),
        (int) noticesPage.getTotalElements(),
        noticesPage.getTotalPages(),
        noticesPage.isLast());
  }

  // 상세 조회
  public NoticeDetailResponseDto getNoticeDetail(Long noticeNo) {
    // 1. 공지 단건 조회
    Notices notice =
        noticesRepository
            .findByNoticesNo(noticeNo)
            .orElseThrow(() -> new CustomException(NoticeException.NOTICE_NOT_FOUND));

    // 2. 조회수 증가
    notice.increaseViewCount();
    // 별도 save() 불필요 (JPA 더티 체킹)

    // 3. 첨부파일 조회
    List<FileResponseDto> files =
        noticeFilesRepository.findAllByNotice(notice).stream()
            .map(
                nf -> {
                  Files file = nf.getFile();
                  return FileResponseDto.of(file, file.getUrl());
                })
            .toList();

    // 4. 이전글 조회
    Page<Notices> prevPage =
        noticesRepository.findPreviousNotice(notice.getCreatedAt(), PageRequest.of(0, 1));
    AdjacentNoticeDto previous =
        prevPage.isEmpty() ? null : mapToAdjacent(prevPage.getContent().get(0));

    // 5. 다음글 조회
    Page<Notices> nextPage =
        noticesRepository.findNextNotice(notice.getCreatedAt(), PageRequest.of(0, 1));
    AdjacentNoticeDto next =
        nextPage.isEmpty() ? null : mapToAdjacent(nextPage.getContent().get(0));

    // 6. 응답 조립
    return new NoticeDetailResponseDto(
        notice.getNoticesNo(),
        notice.getTitle(),
        notice.getCategory().name(),
        notice.getCreatedAt(),
        notice.getPeriodStart().toLocalDate(),
        notice.getPeriodEnd().toLocalDate(),
        notice.getViewCount(),
        notice.getContent(),
        files,
        previous,
        next);
  }

  // 게시글 등록
  public NoticeDetailResponseDto createNotice(NoticeRequestDto dto) {
    // 1. 로그인 유저 조회
    Users currentUser = getCurrentUser();
    if (!currentUser.getRole().equals(Role.ADMIN)) {
      throw new CustomException(UserException.NOT_ADMIN);
    }

    // 2. 공지 저장
    Notices notice =
        Notices.builder()
            .title(dto.title())
            .category(dto.category())
            .periodStart(dto.periodStart() != null ? dto.periodStart().atStartOfDay() : null)
            .periodEnd(dto.periodEnd() != null ? dto.periodEnd().atStartOfDay() : null)
            .content(dto.content())
            .user(currentUser)
            .build();
    noticesRepository.save(notice);

    // 3. 첨부파일 처리
    List<FileResponseDto> fileResponses = List.of();
    if (dto.filesNo() != null) {
      fileResponses =
          dto.filesNo().stream()
              .map(
                  fileNo -> {
                    Files file =
                        filesRepository
                            .findById(fileNo)
                            .orElseThrow(
                                () -> new CustomException(FileSystemExceptionType.FILE_NOT_FOUND));
                    file.activateFile();
                    filesRepository.save(file);

                    NoticeFiles mapping = NoticeFiles.builder().notice(notice).file(file).build();
                    noticeFilesRepository.save(mapping);

                    return FileResponseDto.of(file, file.getUrl());
                  })
              .toList();
    }

    // 4. 이전글/다음글 조회 (createdAt 기준)
    Page<Notices> prevPage =
        noticesRepository.findPreviousNotice(notice.getCreatedAt(), PageRequest.of(0, 1));
    AdjacentNoticeDto previous =
        prevPage.isEmpty() ? null : mapToAdjacent(prevPage.getContent().get(0));

    Page<Notices> nextPage =
        noticesRepository.findNextNotice(notice.getCreatedAt(), PageRequest.of(0, 1));
    AdjacentNoticeDto next =
        nextPage.isEmpty() ? null : mapToAdjacent(nextPage.getContent().get(0));

    // 5. 최종 응답 조립
    return new NoticeDetailResponseDto(
        notice.getNoticesNo(),
        notice.getTitle(),
        notice.getCategory().name(),
        notice.getCreatedAt(),
        notice.getPeriodStart() != null ? notice.getPeriodStart().toLocalDate() : null,
        notice.getPeriodEnd() != null ? notice.getPeriodEnd().toLocalDate() : null,
        notice.getViewCount(),
        notice.getContent(),
        fileResponses,
        previous,
        next);
  }

  private Users getCurrentUser() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    if (!(principal instanceof Users user)) {
      throw new CustomException(UserException.USER_NOT_FOUND);
    }
    return user;
  }

  // 내부 변환 함수
  private AdjacentNoticeDto mapToAdjacent(Notices n) {
    return new AdjacentNoticeDto(n.getNoticesNo(), n.getTitle(), n.getCreatedAt().toString());
  }
}

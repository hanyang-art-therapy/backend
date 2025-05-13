package com.hanyang.arttherapy.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.hanyang.arttherapy.domain.Files;
import com.hanyang.arttherapy.domain.Reviews;
import com.hanyang.arttherapy.domain.Users;
import com.hanyang.arttherapy.domain.enums.FilesType;
import com.hanyang.arttherapy.dto.request.ReviewRequestDto;
import com.hanyang.arttherapy.dto.response.FileResponseDto;
import com.hanyang.arttherapy.dto.response.ReviewResponseDto;
import com.hanyang.arttherapy.repository.FilesRepository;
import com.hanyang.arttherapy.repository.ReviewRepository;
import com.hanyang.arttherapy.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

  private final ReviewRepository reviewRepository;
  private final FilesRepository filesRepository;
  private final UserRepository userRepository;
  private final FileStorageService fileStorageService;

  // 리뷰 조회
  public Page<ReviewResponseDto> getReviews(Long artsNo, Pageable pageable) {
    return reviewRepository
        .findAllByArtsNo(artsNo, pageable)
        .map(
            review -> {
              Users user = getUserByReview(review);
              List<Files> files = findFiles(List.of(review.getFilesNo()));
              List<FileResponseDto> fileResponseDtos = toFileResponseDtos(files);

              return new ReviewResponseDto(
                  review.getReviewsNo(),
                  review.getReviewText(),
                  maskUserName(user.getUserName()),
                  fileResponseDtos);
            });
  }

  // 리뷰 등록
  public ReviewResponseDto createReview(
      Long galleriesNo, Long artsNo, ReviewRequestDto reviewRequestDto) {
    Users user = getUserByUserId();

    // S3에 파일 저장 및 DB에 기록
    List<MultipartFile> multipartFiles = reviewRequestDto.files();
    List<FileResponseDto> storedFiles = fileStorageService.store(multipartFiles, FilesType.REVIEW);

    if (storedFiles.isEmpty()) {
      throw new IllegalArgumentException("파일 업로드에 실패했습니다.");
    }

    // 파일 활성화
    List<Long> filesNoList =
        storedFiles.stream().map(FileResponseDto::filesNo).collect(Collectors.toList());
    List<Files> files = validateFiles(filesNoList); // 유효성 검사
    activateFiles(files); // 활성화 처리

    // DB에 기록된 파일 정보로 Dto 생성
    Long firstFileNo = storedFiles.get(0).filesNo();

    Reviews review =
        Reviews.builder()
            .userNo(user.getUserNo())
            .artsNo(artsNo)
            .filesNo(firstFileNo)
            .reviewText(reviewRequestDto.reviewText())
            .build();

    reviewRepository.save(review);

    return new ReviewResponseDto(
        review.getReviewsNo(),
        review.getReviewText(),
        maskUserName(user.getUserName()),
        storedFiles);
  }

  // 댓글 수정
  public ReviewResponseDto patchReview(
      Long reviewNo, String reviewText, List<MultipartFile> files) {
    Users user = getUserByUserId();

    Reviews review =
        reviewRepository
            .findById(reviewNo)
            .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

    if (reviewText != null) {
      review.updateReviewText(reviewText);
    }
    if (files != null && !files.isEmpty()) {
      List<FileResponseDto> newFiles = fileStorageService.store(files, FilesType.REVIEW);

      // DB에 저장된 파일 활성화 처리
      List<Long> filesNoList =
          newFiles.stream().map(FileResponseDto::filesNo).collect(Collectors.toList());
      List<Files> savedFiles = validateFiles(filesNoList); // 유효성 검사
      activateFiles(savedFiles); // 활성화 처리

      deactivateFile(review.getFilesNo());
      review.updateFilesNo(newFiles.get(0).filesNo());
      reviewRepository.save(review);

      return new ReviewResponseDto(
          review.getReviewsNo(),
          review.getReviewText(),
          maskUserName(user.getUserName()),
          newFiles);
    }

    List<Files> existingFiles = findFiles(List.of(review.getFilesNo()));
    return new ReviewResponseDto(
        review.getReviewsNo(),
        review.getReviewText(),
        maskUserName(user.getUserName()),
        toFileResponseDtos(existingFiles));
  }

  // 댓글 삭제
  public void deleteReview(Long reviewNo) {
    // 리뷰 조회
    Reviews review =
        reviewRepository
            .findById(reviewNo)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리뷰입니다."));

    Long fileNo = review.getFilesNo();
    filesRepository
        .findById(fileNo)
        .ifPresent(
            file -> {
              fileStorageService.deletedFileFromSystem(fileNo);
              file.markAsDeleted();
              filesRepository.save(file);
            });

    reviewRepository.delete(review);
  }

  // 리뷰 작성자의 이름 마스킹 처리
  private String maskUserName(String userName) {
    if (userName.length() <= 1) return "*";
    return userName.charAt(0) + "*".repeat(userName.length() - 1);
  }

  // 유저 조회 메서드
  private Users getUserByReview(Reviews review) {
    return userRepository
        .findById(review.getUserNo())
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
  }

  private Users getUserByUserId() {
    String userId = SecurityContextHolder.getContext().getAuthentication().getName();
    return userRepository
        .findByUserId(userId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
  }

  // 파일 유효성 검사
  private List<Files> validateFiles(List<Long> filesNoList) {
    List<Files> files = filesRepository.findByFilesNoInAndUseYn(filesNoList, false);
    if (files.size() != filesNoList.size()) {
      throw new IllegalArgumentException("일부 파일이 존재하지 않거나 이미 사용 중입니다.");
    }
    return files;
  }

  // 파일 조회 메서드
  private List<Files> findFiles(List<Long> filesNoList) {
    return filesRepository.findByFilesNoIn(filesNoList);
  }

  // 파일 활성화
  private void activateFiles(List<Files> files) {
    files.forEach(Files::activateFile);
    filesRepository.saveAll(files);
  }

  // 파일 비활성화
  private void deactivateFile(Long filesNo) {
    filesRepository.findById(filesNo).ifPresent(Files::markAsDeleted);
  }

  // FileResponseDto 변환
  private List<FileResponseDto> toFileResponseDtos(List<Files> files) {
    return files.stream()
        .map(file -> FileResponseDto.of(file, file.getUrl()))
        .collect(Collectors.toList());
  }
}

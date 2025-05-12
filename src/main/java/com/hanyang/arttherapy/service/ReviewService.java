package com.hanyang.arttherapy.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanyang.arttherapy.domain.Files;
import com.hanyang.arttherapy.domain.Reviews;
import com.hanyang.arttherapy.domain.Users;
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

    List<Long> filesNoList = reviewRequestDto.filesNo();
    if (filesNoList == null || filesNoList.isEmpty()) {
      throw new IllegalArgumentException("파일 번호가 전달되지 않았습니다.");
    }

    Long firstFileNo = filesNoList.get(0);

    Reviews review =
        Reviews.builder()
            .userNo(user.getUserNo())
            .artsNo(artsNo)
            .filesNo(firstFileNo)
            .reviewText(reviewRequestDto.reviewText())
            .build();

    reviewRepository.save(review);

    List<Files> files = validateFiles(filesNoList);
    activateFiles(files);

    List<FileResponseDto> fileResponseDtos = toFileResponseDtos(files);

    return new ReviewResponseDto(
        review.getReviewsNo(),
        review.getReviewText(),
        maskUserName(user.getUserName()),
        fileResponseDtos);
  }

  // 댓글 수정
  public ReviewResponseDto patchReview(Long reviewNo, String reviewText, List<Integer> filesNo) {
    Users user = getUserByUserId();

    Reviews review =
        reviewRepository
            .findById(reviewNo)
            .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

    if (reviewText != null) {
      review.updateReviewText(reviewText);
    }

    if (filesNo != null && !filesNo.isEmpty()) {
      List<Long> filesLongNo = filesNo.stream().map(Long::valueOf).collect(Collectors.toList());

      List<Files> newFiles = validateFiles(filesLongNo);
      deactivateFile(review.getFilesNo());
      activateFiles(newFiles);

      review.updateFilesNo(filesLongNo.get(0));
      reviewRepository.save(review);

      return new ReviewResponseDto(
          review.getReviewsNo(),
          review.getReviewText(),
          maskUserName(user.getUserName()),
          toFileResponseDtos(newFiles));
    }

    // 기존 파일 반환
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

    // 연결된 파일 번호 조회
    Long fileNo = review.getFilesNo();

    // 파일 조회 및 delYn 업데이트
    filesRepository
        .findById(fileNo)
        .ifPresent(
            file -> {
              // 파일의 delYn만 true로 업데이트
              file.markAsDeleted();
              filesRepository.save(file);
            });

    // 리뷰 삭제
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

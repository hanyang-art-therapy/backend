package com.hanyang.arttherapy.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.hanyang.arttherapy.common.exception.CustomException;
import com.hanyang.arttherapy.common.exception.exceptionType.ArtsExceptionType;
import com.hanyang.arttherapy.common.exception.exceptionType.FileSystemExceptionType;
import com.hanyang.arttherapy.common.exception.exceptionType.ReviewException;
import com.hanyang.arttherapy.common.exception.exceptionType.UserException;
import com.hanyang.arttherapy.domain.Arts;
import com.hanyang.arttherapy.domain.Files;
import com.hanyang.arttherapy.domain.Reviews;
import com.hanyang.arttherapy.domain.Users;
import com.hanyang.arttherapy.dto.request.ReviewRequestDto;
import com.hanyang.arttherapy.dto.response.FileResponseDto;
import com.hanyang.arttherapy.dto.response.ReviewResponseDto;
import com.hanyang.arttherapy.repository.*;

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
  private final ArtsRepository artsRepository;
  private final GalleriesRepository galleriesRepository;

  // 리뷰 조회
  public Page<ReviewResponseDto> getReviews(Long artsNo, Pageable pageable) {

    Page<Reviews> reviewsPage = reviewRepository.findAllByArts_ArtsNo(artsNo, pageable);

    if (reviewsPage.isEmpty()) {
      return Page.empty(pageable);
    }

    return reviewsPage.map(
        review -> {
          Users user = review.getUser();
          List<Files> files = new ArrayList<>();

          if (review.getFile() != null) {

            files =
                findFiles(List.of(review.getFile().getFilesNo())).stream()
                    .filter(Files::isUseYn)
                    .collect(Collectors.toList());
          }

          List<FileResponseDto> fileResponseDtos = toFileResponseDtos(files);

          return new ReviewResponseDto(
              review.getReviewsNo(),
              review.getReviewText(),
              user == null ? null : maskUserName(user.getUserName()),
              user == null ? null : user.getUserNo(),
              fileResponseDtos);
        });
  }

  // 리뷰 등록
  public ReviewResponseDto createReview(Long artsNo, ReviewRequestDto reviewRequestDto) {
    try {
      // 유저 정보 가져오기 (로그인하지 않은 경우 null 처리)
      Users user = null;

      try {
        user = getUserByUserId(); // 로그인 시도
      } catch (Exception e) {
        log.info("비로그인 사용자입니다.");
      }

      Arts arts =
          artsRepository
              .findById(artsNo)
              .orElseThrow(() -> new CustomException(ArtsExceptionType.ART_NOT_FOUND));

      if (reviewRequestDto.reviewText() == null || reviewRequestDto.reviewText().isBlank()) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "내용을 작성해주세요");
      }

      // 실제 S3에 업로드된 파일 번호 리스트로 DB에서 파일 조회
      List<Long> filesNoList =
          reviewRequestDto.filesNo() != null ? reviewRequestDto.filesNo() : new ArrayList<>();

      // S3에 실제 업로드된 파일 정보 조회
      List<Files> files = filesRepository.findByFilesNoIn(filesNoList);

      if (files.size() != filesNoList.size()) {
        throw new IllegalArgumentException("일부 파일이 존재하지 않거나 사용 중입니다.");
      }

      // 파일이 있을 경우에만 활성화 처리 (useYn = true)
      if (!files.isEmpty()) {
        files.forEach(Files::activateFile);
        filesRepository.saveAll(files);
      }

      Reviews review =
          Reviews.builder()
              .user(user)
              .arts(arts)
              .file(files.isEmpty() ? null : files.get(0))
              .reviewText(reviewRequestDto.reviewText())
              .build();

      reviewRepository.save(review);

      List<FileResponseDto> fileResponseDtos = toFileResponseDtos(files);
      return new ReviewResponseDto(
          review.getReviewsNo(),
          review.getReviewText(),
          user == null ? null : maskUserName(user.getUserName()),
          user == null ? null : user.getUserNo(),
          fileResponseDtos);
    } catch (Exception e) {

      throw new CustomException(ReviewException.SERVER_ERROR);
    }
  }

  // 댓글 수정
  public ReviewResponseDto patchReview(Long reviewNo, String reviewText, List<Long> filesNo) {

    Reviews review =
        reviewRepository
            .findById(reviewNo)
            .orElseThrow(() -> new CustomException(ReviewException.REVIEW_NOT_FOUND));

    Users currentUser = getUserByUserId();
    if (!review.getUser().equals(currentUser)) {
      throw new CustomException(ReviewException.NOT_REVIEW_OWNER);
    }

    if (reviewText == null || reviewText.isBlank()) {
      throw new CustomException(ReviewException.REVIEW_TEXT_REQUIRED);
    }
    review.updateReviewText(reviewText);

    // 파일 교체 처리
    if (filesNo != null && !filesNo.isEmpty()) {
      List<Files> newFiles = filesRepository.findByFilesNoIn(filesNo);

      if (newFiles.isEmpty()) {
        throw new CustomException(FileSystemExceptionType.FILE_NOT_FOUND);
      }

      // 새로운 파일 활성화
      newFiles.forEach(Files::activateFile);
      filesRepository.saveAll(newFiles);

      // 기존 파일 비활성화
      if (review.getFile() != null) {
        deactivateFile(review.getFile().getFilesNo());
      }

      // 리뷰에 파일 업데이트
      review.updateFilesNo(newFiles.get(0));
      reviewRepository.save(review);

      // 응답 생성
      List<FileResponseDto> fileResponseDtos = toFileResponseDtos(newFiles);
      return new ReviewResponseDto(
          review.getReviewsNo(),
          review.getReviewText(),
          maskUserName(review.getUser().getUserName()),
          review.getUser().getUserNo(),
          fileResponseDtos);
    }

    // 기존 파일이 없으면 빈 리스트로 처리
    if (review.getFile() == null) {
      return new ReviewResponseDto(
          review.getReviewsNo(),
          review.getReviewText(),
          maskUserName(review.getUser().getUserName()),
          review.getUser().getUserNo(),
          List.of());
    }

    // 기존 파일이 존재할 때만 조회
    List<Files> existingFiles = findFiles(List.of(review.getFile().getFilesNo()));
    return new ReviewResponseDto(
        review.getReviewsNo(),
        review.getReviewText(),
        maskUserName(review.getUser().getUserName()),
        review.getUser().getUserNo(),
        toFileResponseDtos(existingFiles));
  }

  // 댓글 삭제
  public void deleteReview(Long reviewNo) {
    // 리뷰 조회
    Reviews review =
        reviewRepository
            .findById(reviewNo)
            .orElseThrow(() -> new CustomException(ReviewException.REVIEW_NOT_FOUND));

    // 현재 로그인한 사용자 정보 가져오기
    Users currentUser = getUserByUserId();

    // 작성자와 현재 로그인한 사용자가 같은지 검증
    if (!review.getUser().equals(currentUser)) {
      throw new CustomException(ReviewException.NOT_REVIEW_DELETE_FAILED);
    }

    // 연결된 파일이 있을 때만 Soft Delete 처리
    if (review.getFile() != null) {
      Long fileNo = review.getFile().getFilesNo();
      filesRepository
          .findById(fileNo)
          .ifPresent(
              file -> {
                try {
                  // 실제 물리적 삭제는 하지 않고, Soft Delete만 처리
                  file.markAsDeleted(); // useYn = false, delYn = true
                  filesRepository.save(file);
                } catch (Exception e) {
                  throw new CustomException(ReviewException.REVIEW_DELETE_FAILED);
                }
              });
    }
    // 리뷰 삭제
    reviewRepository.delete(review);
  }

  // 리뷰 작성자의 이름 마스킹 처리
  private String maskUserName(String userName) {
    if (userName.length() <= 1) {
      return "*";
    } else if (userName.length() == 2) {
      // 이름이 두 글자인 경우 앞 글자만 표시
      return userName.charAt(0) + "*";
    } else {
      // 이름이 세 글자 이상인 경우 첫 글자와 마지막 글자는 보이고, 가운데만 마스킹
      return userName.charAt(0)
          + "*".repeat(userName.length() - 2)
          + userName.charAt(userName.length() - 1);
    }
  }

  private Users getUserByUserId() {
    String userId = SecurityContextHolder.getContext().getAuthentication().getName();
    return userRepository
        .findByUserId(userId)
        .orElseThrow(() -> new CustomException(UserException.USER_NOT_FOUND));
  }

  // 파일 조회 메서드
  private List<Files> findFiles(List<Long> filesNoList) {
    return filesRepository.findByFilesNoIn(filesNoList);
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

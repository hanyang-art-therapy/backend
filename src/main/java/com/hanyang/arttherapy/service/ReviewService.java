package com.hanyang.arttherapy.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    return reviewRepository
        .findAllByArts_ArtsNo(artsNo, pageable)
        .map(
            review -> {
              Users user = getUserByReview(review);

              // 활성화된 파일만 조회
              List<Files> files =
                  findFiles(List.of(review.getFile().getFilesNo())).stream()
                      .filter(Files::isUseYn)
                      .collect(Collectors.toList());

              List<FileResponseDto> fileResponseDtos = toFileResponseDtos(files);

              return new ReviewResponseDto(
                  review.getReviewsNo(),
                  review.getReviewText(),
                  maskUserName(user.getUserName()),
                  fileResponseDtos);
            });
  }

  // 리뷰 등록
  public ReviewResponseDto createReview(Long artsNo, ReviewRequestDto reviewRequestDto) {
    // 유저 정보 가져오기
    Users user = getUserByUserId();

    Arts arts =
        artsRepository
            .findById(artsNo)
            .orElseThrow(() -> new IllegalArgumentException("해당 작품이 존재하지 않습니다."));

    // 실제 S3에 업로드된 파일 번호 리스트로 DB에서 파일 조회
    List<Long> filesNoList = reviewRequestDto.filesNo();

    if (filesNoList == null || filesNoList.isEmpty()) {
      throw new IllegalArgumentException("이미지가 첨부되지 않았습니다.");
    }

    // S3에 실제 업로드된 파일 정보 조회
    List<Files> files = filesRepository.findByFilesNoIn(filesNoList);

    if (files.size() != filesNoList.size()) {
      throw new IllegalArgumentException("일부 파일이 존재하지 않거나 사용 중입니다.");
    }

    // 파일 활성화 처리 (useYn = true)
    files.forEach(Files::activateFile);
    filesRepository.saveAll(files);

    // 리뷰 생성 (첫 번째 파일을 대표 이미지로 설정)
    Reviews review =
        Reviews.builder()
            .user(user)
            .arts(arts)
            .file(files.get(0)) // 첫 번째 파일만 대표 이미지로 등록
            .reviewText(reviewRequestDto.reviewText())
            .build();

    reviewRepository.save(review);

    // 응답 DTO 생성
    List<FileResponseDto> fileResponseDtos = toFileResponseDtos(files);
    return new ReviewResponseDto(
        review.getReviewsNo(),
        review.getReviewText(),
        maskUserName(user.getUserName()),
        fileResponseDtos);
  }

  // 댓글 수정
  public ReviewResponseDto patchReview(Long reviewNo, String reviewText, List<Long> filesNo) {

    Reviews review =
        reviewRepository
            .findById(reviewNo)
            .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

    if (reviewText != null) {
      review.updateReviewText(reviewText);
    }

    // 파일 교체 처리
    if (filesNo != null && !filesNo.isEmpty()) {
      List<Files> newFiles = filesRepository.findByFilesNoIn(filesNo);

      if (newFiles.isEmpty()) {
        throw new IllegalArgumentException("업로드된 파일이 존재하지 않습니다.");
      }

      // 새로운 파일 활성화
      newFiles.forEach(Files::activateFile);
      filesRepository.saveAll(newFiles);

      // 기존 파일 비활성화
      deactivateFile(review.getFile().getFilesNo());

      // 리뷰에 파일 업데이트
      review.updateFilesNo(newFiles.get(0));
      reviewRepository.save(review);

      // 응답 생성
      List<FileResponseDto> fileResponseDtos = toFileResponseDtos(newFiles);
      return new ReviewResponseDto(
          review.getReviewsNo(),
          review.getReviewText(),
          maskUserName(review.getUser().getUserName()),
          fileResponseDtos);
    }

    // 파일이 없다면 기존 파일 응답
    List<Files> existingFiles = findFiles(List.of(review.getFile().getFilesNo()));
    return new ReviewResponseDto(
        review.getReviewsNo(),
        review.getReviewText(),
        maskUserName(review.getUser().getUserName()),
        toFileResponseDtos(existingFiles));
  }

  // 댓글 삭제
  public void deleteReview(Long reviewNo) {
    // 리뷰 조회
    Reviews review =
        reviewRepository
            .findById(reviewNo)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리뷰입니다."));

    // 연결된 파일 Soft Delete 처리
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
                e.printStackTrace();
              }
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
        .findById(review.getUser().getUserNo())
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
  }

  private Users getUserByUserId() {
    String userId = "user1";
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

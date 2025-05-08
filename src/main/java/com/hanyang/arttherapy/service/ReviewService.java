package com.hanyang.arttherapy.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.hanyang.arttherapy.domain.Files;
import com.hanyang.arttherapy.domain.Review;
import com.hanyang.arttherapy.dto.response.FileResponseDto;
import com.hanyang.arttherapy.dto.response.ReviewResponseDto;
import com.hanyang.arttherapy.repository.FilesRepository;
import com.hanyang.arttherapy.repository.ReviewRepository;

@Service
public class ReviewService {

  private final ReviewRepository reviewRepository;
  private final FilesRepository filesRepository;

  public ReviewService(ReviewRepository reviewRepository, FilesRepository filesRepository) {
    this.reviewRepository = reviewRepository;
    this.filesRepository = filesRepository;
  }

  // 리뷰 작성자의 이름 마스킹 처리
  private String maskUserName(String userName) {
    if (userName.length() > 1) {
      return userName.charAt(0) + "**";
    } else {
      return "*"; // 이름이 한 글자라면 그냥 *
    }
  }

  public Page<ReviewResponseDto> getReviews(Long artsNo, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);

    Page<Review> reviews = reviewRepository.findAllByArtsNo(artsNo, pageable);

    return reviews.map(
        review -> {
          String userName =
              Optional.ofNullable(review.getUser())
                  .map(user -> maskUserName(user.getUserName()))
                  .orElse("Unknown User");

          List<Long> filesNoList = reviewRepository.findFileIdsByReviewNo(review.getReviewNo());
          List<Files> fileDetails =
              filesNoList.isEmpty()
                  ? List.of()
                  : filesRepository.findByFilesNoInAndUseYn(filesNoList, true);

          List<FileResponseDto> fileDtos =
              fileDetails.stream()
                  .map(
                      file ->
                          new FileResponseDto(
                              file.getFilesNo(),
                              file.getName(),
                              file.getUrl(),
                              file.getFilesSize(),
                              file.getExtension(),
                              file.getFilesType()))
                  .collect(Collectors.toList());

          return ReviewResponseDto.builder()
              .reviewNo(review.getReviewNo())
              .artsNo(artsNo)
              .reviewText(review.getReviewText())
              .createdAt(review.getCreatedAt())
              .userName(userName)
              .files(fileDtos)
              .build();
        });
  }
}

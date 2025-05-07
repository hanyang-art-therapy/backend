package com.hanyang.arttherapy.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.hanyang.arttherapy.domain.Review;
import com.hanyang.arttherapy.dto.response.FileResponseDto;
import com.hanyang.arttherapy.dto.response.ReviewResponseDto;
import com.hanyang.arttherapy.repository.ReviewRepository;

@Service
public class ReviewService {

  private final ReviewRepository reviewRepository;

  // 리뷰 작성자의 이름 마스킹 처리
  private String maskUserName(String userName) {
    if (userName.length() > 1) {
      return userName.charAt(0) + "**";
    } else {
      return "*"; // 이름이 한 글자라면 그냥 *
    }
  }

  public ReviewService(ReviewRepository reviewRepository) {
    this.reviewRepository = reviewRepository;
  }

  public Page<ReviewResponseDto> getReviews(Long artsNo, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);

    Page<Review> reviews = reviewRepository.findAllByArtsNo(artsNo, pageable);

    return reviews.map(
        review -> {
          String userName =
              Optional.ofNullable(review.getUser())
                  .map(user -> maskUserName(user.getUserName())) // 마스킹 로직
                  .orElse("Unknown User");

          FileResponseDto fileDto =
              Optional.ofNullable(review.getFile()).map(FileResponseDto::of).orElse(null);

          Long artsId = review.getArts() != null ? review.getArts().getArtsNo() : null;

          return ReviewResponseDto.builder()
              .reviewNo(review.getReviewNo())
              .artsNo(artsId)
              .reviewText(review.getReviewText())
              .createdAt(review.getCreatedAt())
              .userName(userName)
              .file(fileDto)
              .build();
        });
  }
}

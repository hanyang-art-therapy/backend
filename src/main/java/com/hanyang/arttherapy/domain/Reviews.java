package com.hanyang.arttherapy.domain;

import jakarta.persistence.*;

import com.hanyang.arttherapy.common.entity.BaseEntity;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Builder
@Table(name = "reviews")
public class Reviews extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long reviewsNo;

  @Column(nullable = false)
  private Long artsNo;

  @Column(nullable = false)
  private Long filesNo;

  @Column(nullable = false)
  private Long userNo;

  @Column(columnDefinition = "LONGTEXT")
  private String reviewText;

  public Reviews(Long userNo, Long artsNo, Long filesNo, String reviewText) {
    this.userNo = userNo;
    this.artsNo = artsNo;
    this.filesNo = filesNo;
    this.reviewText = reviewText;
  }

  public void updateReviewText(String reviewText) {
    this.reviewText = reviewText;
  }

  public void updateFilesNo(Long filesNo) {
    this.filesNo = filesNo;
  }
}

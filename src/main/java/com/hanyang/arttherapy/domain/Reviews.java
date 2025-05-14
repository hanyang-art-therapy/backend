// package com.hanyang.arttherapy.domain;
//
// import jakarta.persistence.*;
//
// import com.hanyang.arttherapy.common.entity.BaseEntity;
//
// import lombok.*;
//
// @Getter
// @NoArgsConstructor(access = AccessLevel.PROTECTED)
// @AllArgsConstructor
// @Entity
// @Builder
// public class Reviews extends BaseEntity {
//
//  @Id
//  @GeneratedValue(strategy = GenerationType.IDENTITY)
//  @Column(name = "reviews_no")
//  private Long reviewsNo;
//
//  @ManyToOne
//  @JoinColumn(name = "arts_no")
//  private Arts arts;
//
//  @OneToOne
//  @JoinColumn(name = "files_no")
//  private Files file;
//
//  @ManyToOne
//  @JoinColumn(name = "user_no")
//  private Users user;
//
//  @Column(columnDefinition = "LONGTEXT")
//  private String reviewText;
//
//  public void updateReviewText(String reviewText) {
//    this.reviewText = reviewText;
//  }
//
//  public void updateFilesNo(Files file) {
//    this.file = file;
//  }
// }

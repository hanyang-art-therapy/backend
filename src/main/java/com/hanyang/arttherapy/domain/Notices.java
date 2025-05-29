package com.hanyang.arttherapy.domain;

import java.time.LocalDate;

import jakarta.persistence.*;

import com.hanyang.arttherapy.common.entity.BaseEntity;
import com.hanyang.arttherapy.domain.enums.NoticeCategory;

import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notices extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long noticesNo;

  @ManyToOne
  @JoinColumn(name = "user_no")
  private Users user;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private NoticeCategory category;

  @Column(nullable = false)
  private Integer viewCount = 0;

  @Column private LocalDate periodStart;

  @Column private LocalDate periodEnd;

  @Column(nullable = false)
  private boolean isFixed;

  @Builder
  public Notices(
      String title,
      String content,
      Users user,
      NoticeCategory category,
      LocalDate periodStart,
      LocalDate periodEnd,
      boolean isFixed) {
    this.title = title;
    this.content = content;
    this.user = user;
    this.category = category;
    this.periodStart = periodStart;
    this.periodEnd = periodEnd;
    this.viewCount = 0;
    this.isFixed = isFixed;
  }

  public void update(
      String title,
      String content,
      NoticeCategory category,
      LocalDate periodStart,
      LocalDate periodEnd,
      boolean isFixed) {
    this.title = title;
    this.content = content;
    this.category = category;
    this.periodStart = periodStart;
    this.periodEnd = periodEnd;
    this.isFixed = isFixed;
  }

  public void increaseViewCount() {
    this.viewCount++;
  }
}

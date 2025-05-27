package com.hanyang.arttherapy.domain;

import java.time.LocalDateTime;

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

  @Column private LocalDateTime periodStart;

  @Column private LocalDateTime periodEnd;

  @Builder
  public Notices(
      String title,
      String content,
      Users user,
      NoticeCategory category,
      LocalDateTime periodStart,
      LocalDateTime periodEnd) {
    this.title = title;
    this.content = content;
    this.user = user;
    this.category = category;
    this.periodStart = periodStart;
    this.periodEnd = periodEnd;
    this.viewCount = 0;
  }

  public void increaseViewCount() {
    this.viewCount++;
  }
}

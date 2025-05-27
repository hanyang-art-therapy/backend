package com.hanyang.arttherapy.domain;

import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeFiles {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long noticeFileNo;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "noticeNo", nullable = false)
  private Notices notice;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "filesNo", nullable = false)
  private Files file;

  @Builder
  public NoticeFiles(Notices notice, Files file) {
    this.notice = notice;
    this.file = file;
  }
}

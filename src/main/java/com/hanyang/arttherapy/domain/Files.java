package com.hanyang.arttherapy.domain;

import jakarta.persistence.*;

import com.hanyang.arttherapy.common.entity.BaseEntity;
import com.hanyang.arttherapy.domain.enums.FilesType;

import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Files extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long filesNo;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, length = 512)
  private String url;

  @Column(nullable = false)
  private Long filesSize;

  @Column(nullable = false)
  private boolean useYn;

  @Column(nullable = false)
  private boolean delYn;

  @Column(length = 60, nullable = false)
  private String extension;

  @Enumerated(EnumType.STRING)
  private FilesType filesType;

  public void markAsDeleted() {
    this.useYn = false;
    this.delYn = true;
  }

  public void activateFile() {
    this.useYn = true;
    this.delYn = false;
  }
}

package com.hanyang.arttherapy.domain;

import jakarta.persistence.*;

import com.hanyang.arttherapy.common.entity.BaseEntity;
import com.hanyang.arttherapy.domain.enums.ArtType;

import lombok.*;

@Getter
@Entity
@Table(name = "arts")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Arts extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long artsNo;

  @Column(nullable = false)
  private Long galleriesNo;

  @Column(nullable = false)
  private Long filesNo;

  @Column(nullable = false, length = 500)
  private String artName;

  @Column(length = 500)
  private String caption;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private ArtType artType;

  public void updateArts(Long filesNo, String artName, String caption, ArtType artType) {
    this.filesNo = filesNo;
    this.artName = artName;
    this.caption = caption;
    this.artType = artType;
  }
}

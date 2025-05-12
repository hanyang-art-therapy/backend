package com.hanyang.arttherapy.domain;

import jakarta.persistence.*;

import com.hanyang.arttherapy.common.entity.BaseEntity;
import com.hanyang.arttherapy.domain.enums.ArtType;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "arts")
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
}

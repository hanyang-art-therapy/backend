package com.hanyang.arttherapy.domain;

import java.util.List;

import jakarta.persistence.*;

import com.hanyang.arttherapy.common.entity.BaseEntity;
import com.hanyang.arttherapy.domain.enums.ArtType;

import lombok.*;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Arts extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "arts_no")
  private Long artsNo;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "galleries_no")
  private Galleries galleries;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "files_no")
  private Files file;

  @OneToMany(mappedBy = "arts", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ArtArtistRel> artArtistRels;

  @Column(name = "art_name", nullable = false, length = 500)
  private String artName;

  @Column(name = "caption", length = 500)
  private String caption;

  @Column(name = "co_description", columnDefinition = "LONGTEXT")
  private String coDescription;

  @Enumerated(EnumType.STRING)
  @Column(name = "art_type", nullable = false, length = 20)
  private ArtType artType;

  public void updateArts(Files file, String artName, String caption, ArtType artType) {
    this.file = file;
    this.artName = artName;
    this.caption = caption;
    this.artType = artType;
  }
}

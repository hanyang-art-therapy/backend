package com.hanyang.arttherapy.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.hanyang.arttherapy.domain.enums.ArtType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "arts")
public class Arts {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long artsNo;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "galleriesNo", nullable = false)
  private Galleries galleries;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "filesNo", nullable = false)
  private Files file;

  @Column(nullable = false, length = 500)
  private String artName;

  @Column(length = 500)
  private String caption;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ArtType artType;

  @Column(nullable = false)
  private LocalDateTime uploadedAt;

  @OneToMany(mappedBy = "arts", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @Fetch(FetchMode.SUBSELECT)
  private List<ArtArtistRel> artArtistRelList = new ArrayList<>();

  @OneToMany(mappedBy = "arts", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @Fetch(FetchMode.SUBSELECT)
  private List<Review> reviews = new ArrayList<>();

  @PrePersist
  public void setUploadedAt() {
    this.uploadedAt = LocalDateTime.now();
  }
}

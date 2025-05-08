package com.hanyang.arttherapy.domain;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "art_artist_rel")
public class ArtArtistRel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long artArtistRelNo;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "artistsNo", nullable = false)
  private Artists artist;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "artsNo", nullable = false)
  private Arts arts;

  @Column(columnDefinition = "LONGTEXT")
  private String description;
}

package com.hanyang.arttherapy.domain;

import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "art_artist_rel")
public class ArtArtistRel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long artArtistRelNo;

  @Column(nullable = false)
  private Long artistsNo;

  @Column(nullable = false)
  private Long artsNo;

  @Column(columnDefinition = "LONGTEXT")
  private String description;
}

// package com.hanyang.arttherapy.domain;
//
// import jakarta.persistence.*;
//
// import lombok.*;
//
// @Getter
// @NoArgsConstructor(access = AccessLevel.PROTECTED)
// @AllArgsConstructor
// @Entity
// @Builder
// public class ArtArtistRel {
//
//  @Id
//  @GeneratedValue(strategy = GenerationType.IDENTITY)
//  private Long artArtistRelNo;
//
//  @ManyToOne
//  @JoinColumn(name = "arts_no")
//  private Arts arts;
//
//  @ManyToOne
//  @JoinColumn(name = "artist_no")
//  private Artists artists;
//
//  @Column(columnDefinition = "LONGTEXT")
//  private String description;
// }

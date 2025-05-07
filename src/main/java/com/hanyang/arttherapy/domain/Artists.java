package com.hanyang.arttherapy.domain;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Artists {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long artistsNo;

  @Column(nullable = false)
  private String artistName;

  @Column(unique = true, nullable = false, length = 10)
  private String studentNo;

  @Column(nullable = false)
  private int cohort;

  @OneToMany(mappedBy = "artist", fetch = FetchType.LAZY)
  private List<ArtArtistRel> artArtistRels;

  public void updateArtistInfo(
      Optional<String> artistName, Optional<String> studentNo, Optional<Integer> cohort) {
    artistName.ifPresent(value -> this.artistName = value);
    studentNo.ifPresent(value -> this.studentNo = value);
    cohort.ifPresent(value -> this.cohort = value);
  }
}

package com.hanyang.arttherapy.domain;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "galleries")
public class Galleries {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long galleriesNo;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private LocalDateTime startDate;

  @Column(nullable = false)
  private LocalDateTime endDate;

  public void update(String title, LocalDateTime startDate, LocalDateTime endDate) {
    this.title = title;
    this.startDate = startDate;
    this.endDate = endDate;
  }
}

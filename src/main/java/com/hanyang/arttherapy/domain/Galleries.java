package com.hanyang.arttherapy.domain;

import java.time.LocalDate;

import jakarta.persistence.*;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "galleries")
public class Galleries {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long galleriesNo;

  @Column(nullable = false, length = 255)
  private String title;

  @Column(nullable = false)
  private LocalDate startDate;

  @Column(nullable = false)
  private LocalDate endDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_no")
  private Users user;

  public void update(String title, LocalDate startDate, LocalDate endDate) {
    this.title = title;
    this.startDate = startDate;
    this.endDate = endDate;
  }
}

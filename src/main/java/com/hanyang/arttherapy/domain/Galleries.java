package com.hanyang.arttherapy.domain;

import java.time.LocalDateTime;

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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_no", nullable = false)
  private Users user;

  @Column(nullable = false, length = 255)
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

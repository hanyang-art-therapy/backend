package com.hanyang.arttherapy.domain;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "reviews")
public class Review {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long reviewNo;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "userNo", nullable = false)
  private Users user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "artsNo", nullable = false)
  private Arts arts;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "filesNo", nullable = false)
  private Files file;

  @Column(columnDefinition = "LONGTEXT")
  private String reviewText;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @PrePersist
  public void setCreatedAt() {
    this.createdAt = LocalDateTime.now();
  }
}

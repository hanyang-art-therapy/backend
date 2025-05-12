package com.hanyang.arttherapy.domain;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import lombok.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "refreshToken")
public class RefreshToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long refreshTokenNo;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "userNo", nullable = false, unique = true)
  private Users users;

  @Column(nullable = false, unique = true)
  private String refreshToken;

  @Column(nullable = false)
  private LocalDateTime expiredAt;

  @Column(nullable = false)
  private String userAgent;

  @Column(nullable = false)
  private String ip;

  @PrePersist
  public void setExpiredAt() {
    this.expiredAt = LocalDateTime.now().plusDays(7);
  }
}

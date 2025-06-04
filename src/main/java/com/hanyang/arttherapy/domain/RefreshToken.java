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
@Table(name = "refresh_token")
public class RefreshToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long refreshTokenNo;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_no", nullable = false)
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
  public void updateExpiredAt() {
    if (this.expiredAt == null) {
      this.expiredAt = LocalDateTime.now().plusDays(7);
    }
  }
}

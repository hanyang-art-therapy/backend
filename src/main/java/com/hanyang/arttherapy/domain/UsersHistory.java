package com.hanyang.arttherapy.domain;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import jakarta.persistence.*;

import com.hanyang.arttherapy.domain.enums.UserStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users_history")
public class UsersHistory {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userHistoryNo;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UserStatus userStatus;

  @Column(nullable = false)
  private Timestamp signupTimestamp;

  @Column private Timestamp signoutTimestamp;

  @Column private Timestamp bannedTimestamp;

  @Column private String cause;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_no", nullable = false) // DB의 외래 키 컬럼명
  private Users user;

  @PrePersist
  public void setDefaults() {
    this.userStatus = UserStatus.ACTIVE;
    this.signupTimestamp = Timestamp.valueOf(LocalDateTime.now().withNano(0)); // 밀리초 제거
  }

  public void setBannedNow() {
    this.bannedTimestamp = Timestamp.valueOf(LocalDateTime.now().withNano(0)); // 밀리초 제거
  }

  public void setSignoutTimestamp() {
    this.signoutTimestamp = Timestamp.valueOf(LocalDateTime.now().withNano(0)); // 밀리초 제거
  }
}

package com.hanyang.arttherapy.domain;

import java.sql.Timestamp;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "usersHistory")
public class UsersHistory {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userHistoryNo;

  @Column(nullable = false)
  private String userStatus;

  @Column(nullable = false)
  private Timestamp userEventTimestamp;

  @Column(nullable = false)
  private Timestamp userHistory;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "userNo", nullable = false) // DB의 외래 키 컬럼명
  private Users user;
}

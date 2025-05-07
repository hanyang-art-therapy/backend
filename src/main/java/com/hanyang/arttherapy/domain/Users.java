package com.hanyang.arttherapy.domain;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
public class Users {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userNo;

  @Column(nullable = false, unique = true)
  private String userId;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String userName;

  @Column(nullable = true, unique = true)
  private String studentNo;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UserStatus userStatus;

  @PrePersist
  public void setDefaults() {

    if (studentNo == null || studentNo.trim().isEmpty()) {
      this.role = Role.USER; // 학번이 없으면 USER로 설정
    } else {
      this.role = Role.ARTIST; // 학번이 있으면 ARTIST 설정
    }

    this.userStatus = UserStatus.ACTIVE; // userState를 기본값 ACTIVE로 설정
  }
}

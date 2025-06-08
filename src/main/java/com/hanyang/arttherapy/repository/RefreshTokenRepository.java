package com.hanyang.arttherapy.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.hanyang.arttherapy.domain.RefreshTokens;
import com.hanyang.arttherapy.domain.Users;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokens, Long> {

  Optional<RefreshTokens> findByIpAndUserAgent(String ip, String userAgent);

  Optional<RefreshTokens> findByUsers(Users users);

  Optional<RefreshTokens> findByRefreshToken(String refreshToken);

  // Users의 userNo 필드를 기준으로 찾고 싶다면
  Optional<RefreshTokens> findByUsers_UserNo(Long userNo);

  Optional<RefreshTokens> findByIpAndUserAgentAndRefreshToken(
      String ip, String userAgent, String refreshToken);

  // 해당 유저의 컬럼 하나만 값 비우기
  @Modifying
  // JPQL사용
  @Query(
      "UPDATE RefreshTokens rt SET rt.refreshToken = null WHERE rt.ip = :ip AND rt.userAgent = :userAgent AND rt.users.userNo = :userNo")
  void clearRefreshToken(String ip, String userAgent, Long userNo);
}

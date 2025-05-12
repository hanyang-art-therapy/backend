package com.hanyang.arttherapy.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hanyang.arttherapy.domain.RefreshToken;
import com.hanyang.arttherapy.domain.Users;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

  Optional<RefreshToken> findByIpAndUserAgent(String ip, String userAgent);

  Optional<RefreshToken> findByIpAndUserAgentAndRefreshToken(
      String ip, String userAgent, String refreshToken);

  Optional<RefreshToken> findByUsers(Users users);
}

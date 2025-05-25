package com.hanyang.arttherapy.common.util;

import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.hanyang.arttherapy.domain.Users;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtUtil {

  private final String secretKey =
      Base64.getEncoder()
          .encodeToString("yoursecretlongandsecuresecretkeymustbeatleast32bywcg".getBytes());

  private final long accessTokenValidity = 1000L * 60; // 1분

  public String createAccessToken(Users user) {
    return createToken(user.getUserNo(), accessTokenValidity);
  }

  public String createRefreshToken(Users user) {
    return UUID.randomUUID().toString();
  }

  // 리프레시 토큰을 생성하고 쿠키에 담는 메서드
  public void addRefreshTokenToCookie(HttpServletResponse httpResponse, String refreshToken) {

    Cookie cookie = new Cookie("refreshToken", refreshToken);
    cookie.setHttpOnly(true); // 보안을 위해 HttpOnly 설정
    cookie.setSecure(false); // HTTPS 연결에서만 쿠키 전송
    cookie.setPath("/"); // 전체 경로에서 유효하도록 설정
    cookie.setMaxAge(7 * 24 * 60 * 60); // 7일 동안 유효

    httpResponse.addCookie(cookie); // 응답에 쿠키 추가
  }

  // access토큰 생성 로직
  private String createToken(Long userId, long expirationTime) {
    Claims claims = Jwts.claims().setSubject(userId.toString());
    Date now = new Date();
    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + expirationTime))
        .signWith(SignatureAlgorithm.HS256, secretKey)
        .compact();
  }

  // 토큰 유효성 검사
  public boolean validateToken(String token) {
    try {
      Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  // 토큰에서 userNo 추출
  public Long getUserIdFromToken(String token) {
    Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    return Long.parseLong(claims.getSubject());
  }

  public static String refreshTokenFromCookie(HttpServletRequest httpRequest) {
    // 쿠키가 없거나 빈 배열인 경우
    if (httpRequest.getCookies() == null || httpRequest.getCookies().length == 0) {
      return null; // 쿠키가 없을 경우 null 반환
    }

    for (Cookie cookie : httpRequest.getCookies()) {
      if ("refreshToken".equals(cookie.getName())) {
        return cookie.getValue(); // 리프레시 토큰 값 리턴
      }
    }
    return null; // 리프레시 토큰이 없을 경우 null 반환
  }

  public void deleteRefreshTokenCookie(HttpServletResponse response) {
    Cookie cookie = new Cookie("refreshToken", null);
    cookie.setMaxAge(0); // 즉시 만료
    cookie.setPath("/");
    cookie.setHttpOnly(false);
    response.addCookie(cookie);
  }
}

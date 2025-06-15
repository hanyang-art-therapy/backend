package com.hanyang.arttherapy.common.util;

import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import com.hanyang.arttherapy.domain.Users;
import com.hanyang.arttherapy.domain.enums.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtUtil {

  private final SecretKey secretKey =
      new SecretKeySpec(
          Base64.getDecoder().decode("yoursecretlongandsecuresecretkeymustbeatleast32bywcg"),
          SignatureAlgorithm.HS256.getJcaName());

  private final long accessTokenValidity = 1000L * 60 * 60 * 10; // 10시간

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

  public String createAccessToken(Users user) {
    return createToken(user.getUserNo(), user.getRole().name(), accessTokenValidity);
  }

  public String createRefreshToken(Users user) {
    return UUID.randomUUID().toString();
  }

  // 리프레시 토큰을 생성하고 쿠키에 담는 메서드
  public void addRefreshTokenToCookie(HttpServletResponse httpResponse, String refreshToken) {
    ResponseCookie cookie =
        ResponseCookie.from("refreshToken", refreshToken)
            .httpOnly(true)
            .secure(true) // HTTPS면 true
            .sameSite("None") // ← 인증,인가를 위해 중요!
            .path("/")
            .maxAge(7 * 24 * 60 * 60)
            .build();
    //
    //    System.out.println("[JwtUtil] Set-Cookie header: " + cookie.toString());

    httpResponse.addHeader("Set-Cookie", cookie.toString());
  }

  // access토큰 생성 로직
  private String createToken(Long userId, String role, long expirationTime) {
    Claims claims = Jwts.claims().setSubject(userId.toString());
    claims.put("role", role);

    Date now = new Date();
    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + expirationTime))
        .signWith(secretKey, SignatureAlgorithm.HS256)
        .compact();
  }

  // 토큰 유효성 검사
  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
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

  // 토큰에서 Role 추출
  public Role getRoleFromToken(String token) {
    Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    String roleStr = (String) claims.get("role");
    return Role.valueOf(roleStr);
  }

  public ResponseCookie deleteRefreshTokenCookie() {
    return ResponseCookie.from("refreshToken", "")
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(0)
        .build();
  }
}

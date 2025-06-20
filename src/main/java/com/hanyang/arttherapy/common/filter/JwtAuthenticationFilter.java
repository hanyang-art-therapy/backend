package com.hanyang.arttherapy.common.filter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.hanyang.arttherapy.common.util.JwtUtil;
import com.hanyang.arttherapy.domain.RefreshTokens;
import com.hanyang.arttherapy.domain.Users;
import com.hanyang.arttherapy.domain.enums.Role;
import com.hanyang.arttherapy.repository.RefreshTokenRepository;
import com.hanyang.arttherapy.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final UserRepository userRepository;
  private final RefreshTokenRepository refreshTokenRepository;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    // 1. OPTIONS 요청은 필터 통과 (CORS Preflight)
    if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
      filterChain.doFilter(request, response);
      return;
    }

    // 2. Access Token 추출
    String authHeader = request.getHeader("Authorization");
    String token =
        (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;

    if (token == null) {
      filterChain.doFilter(request, response);
      return;
    }

    // 3. Access Token 유효성 검사
    if (!jwtUtil.validateToken(token)) {
      // Access Token 만료 → Refresh Token으로 재발급 시도
      String refreshToken = JwtUtil.refreshTokenFromCookie(request);

      if (refreshToken != null) {
        Optional<RefreshTokens> existingTokenOpt =
            refreshTokenRepository.findByRefreshToken(refreshToken);

        if (existingTokenOpt.isPresent()) {
          RefreshTokens existingToken = existingTokenOpt.get();

          String ip = request.getRemoteAddr();
          String userAgent = request.getHeader("User-Agent");

          boolean isExpired = existingToken.getExpiredAt().isBefore(LocalDateTime.now());
          boolean isDifferentIpOrAgent =
              !existingToken.getIp().equals(ip) || !existingToken.getUserAgent().equals(userAgent);

          if (!isExpired && !isDifferentIpOrAgent) {
            Users user = existingToken.getUsers();

            // 새 Access Token 발급
            String newAccessToken = jwtUtil.createAccessToken(user);
            response.setHeader("Authorization", "Bearer " + newAccessToken);
            jwtUtil.addRefreshTokenToCookie(response, refreshToken);

            // SecurityContext 설정 (중복 방지)
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
              CustomUserDetail userDetails = new CustomUserDetail(user);
              UsernamePasswordAuthenticationToken authentication =
                  new UsernamePasswordAuthenticationToken(
                      userDetails, null, userDetails.getAuthorities());
              authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
              SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);
            return;
          } else {
            // 만료되었거나 정보 불일치 → 삭제
            refreshTokenRepository.delete(existingToken);
          }
        }
      }

      // Refresh Token도 실패 → 인증 실패
      SecurityContextHolder.clearContext();
      response.setContentType("application/json");
      response
          .getWriter()
          .write("{\"error\": \"Access token expired and refresh token invalid or missing.\"}");
      return;
    }

    // 4. Access Token 유효한 경우 → 사용자 인증 처리
    Long userId = jwtUtil.getUserIdFromToken(token);
    Role role = jwtUtil.getRoleFromToken(token);

    Optional<Users> userOptional = userRepository.findById(userId);
    if (userOptional.isEmpty()) {
      filterChain.doFilter(request, response);
      return;
    }

    Users user = userOptional.get();

    // 토큰의 Role과 실제 Role이 다르면 인증 처리하지 않음 (선택 보안)
    if (user.getRole() != role) {
      filterChain.doFilter(request, response);
      return;
    }

    // 이미 인증된 상태가 아니라면 SecurityContext 설정
    if (SecurityContextHolder.getContext().getAuthentication() == null) {
      CustomUserDetail userDetails = new CustomUserDetail(user);
      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // 5. 필터 체인 진행
    filterChain.doFilter(request, response);
  }
}

package com.hanyang.arttherapy.common.filter;

import java.io.IOException;
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
import com.hanyang.arttherapy.domain.Users;
import com.hanyang.arttherapy.domain.enums.Role;
import com.hanyang.arttherapy.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final UserRepository userRepository;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    // CORS preflight 요청은 바로 통과시켜야 함
    if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
      filterChain.doFilter(request, response);
      return;
    }

    // 1. Authorization 헤더에서 토큰 추출
    String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response); // 다음 필터로 넘김
      return;
    }

    // 2. 토큰 파싱 및 검증
    String token = authHeader.substring(7); // "Bearer " 제거
    if (!jwtUtil.validateToken(token)) {
      filterChain.doFilter(request, response);
      return;
    }

    // 3. 사용자 정보 조회
    Long userId = jwtUtil.getUserIdFromToken(token);
    Role role = jwtUtil.getRoleFromToken(token);

    Optional<Users> userOptional = userRepository.findById(userId);
    if (userOptional.isEmpty()) {
      filterChain.doFilter(request, response);
      return;
    }

    Users user = userOptional.get();

    // 토큰의 역할과 실제 유저 역할이 다를 경우 거부 (선택적 보안 강화)
    if (user.getRole() != role) {
      filterChain.doFilter(request, response);
      return;
    }

    CustomUserDetail userDetails = new CustomUserDetail(user);

    // 4. 인증 객체 생성 및 SecurityContext에 저장
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    SecurityContextHolder.getContext().setAuthentication(authentication);

    // 5. 다음 필터 진행
    filterChain.doFilter(request, response);
  }
}

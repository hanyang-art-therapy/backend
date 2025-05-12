package com.hanyang.arttherapy.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.hanyang.arttherapy.common.exception.CustomException;
import com.hanyang.arttherapy.common.exception.exceptionType.UserException;
import com.hanyang.arttherapy.common.util.JwtUtil;
import com.hanyang.arttherapy.domain.RefreshToken;
import com.hanyang.arttherapy.domain.Users;
import com.hanyang.arttherapy.dto.request.userRequest.*;
import com.hanyang.arttherapy.dto.response.userResponse.*;
import com.hanyang.arttherapy.repository.RefreshTokenRepository;
import com.hanyang.arttherapy.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
  @Autowired private JavaMailSender mailSender;
  private final UserRepository userRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final JwtUtil jwtUtil;
  private final BCryptPasswordEncoder bCryptpasswordEncoder;

  public boolean existsByUserId(String userId) {
    return userRepository.existsByUserId(userId);
  }

  public boolean existsByEmail(String email) {
    return userRepository.existsByEmail(email);
  }

  public boolean existsByStudentNo(String studentNo) {
    return userRepository.existsByStudentNo(studentNo);
  }

  // 아이디 찾기
  public String findByEmailAndUserName(IdRequest request) {
    Users user =
        userRepository
            .findByEmailAndUserName(request.email(), request.userName())
            .orElseThrow(() -> new CustomException(UserException.USER_NOT_FOUND));

    String originalId = user.getUserId();
    String maskedId = maskUserId(originalId);

    return maskedId;
  }

  // 아이디 *처리
  private String maskUserId(String originalId) {
    if (originalId.length() <= 4) {
      return originalId;
    }
    return originalId.substring(0, 4) + "*".repeat(originalId.length() - 4);
  }

  // 비밀번호 찾기
  public String newPassword(String userId, String email) {
    //    System.out.println(">>> newPassword called with userId: " + userId + ", email: " + email);
    Optional<Users> userOpt = userRepository.findByUserIdAndEmail(userId, email);

    if (userOpt.isEmpty()) {
      throw new CustomException(UserException.USER_NOT_FOUND);
    }

    Users user = userOpt.get();

    // 임시 비밀번호 생성
    String temporaryPassword = generateTemporaryPassword();

    // 임시 비밀번호를 암호화하여 저장
    user.setPassword(bCryptpasswordEncoder.encode(temporaryPassword));
    userRepository.save(user);

    // 이메일로 임시 비밀번호 전송
    sendTemporaryPasswordEmail(email, temporaryPassword);

    return "임시 비밀번호가 이메일로 전송되었습니다.";
  }

  // 임시 비밀번호 생성 (예: 10자리 랜덤 비밀번호)
  private String generateTemporaryPassword() {
    int length = 10;
    String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~!@#$%^&*";
    Random random = new Random();
    StringBuilder sb = new StringBuilder(length);

    for (int i = 0; i < length; i++) {
      int index = random.nextInt(chars.length());
      sb.append(chars.charAt(index));
    }

    return sb.toString();
  }

  // 이메일로 임시 비밀번호 보내기
  private void sendTemporaryPasswordEmail(String email, String temporaryPassword) {
    try {
      SimpleMailMessage message = new SimpleMailMessage();
      message.setTo(email);
      message.setSubject("임시 비밀번호 안내");
      message.setText("안녕하세요.\n\n임시 비밀번호는 " + temporaryPassword + " 입니다.");
      message.setFrom("mingke48@gmail.com");
      mailSender.send(message);
    } catch (Exception e) {
      throw new CustomException(UserException.EMAIL_SEND_FAIL);
    }
  }

  public String resetPassword(PasswordResetRequest request) {

    Users user =
        userRepository
            .findByUserId(request.userId())
            .orElseThrow(() -> new CustomException(UserException.USER_NOT_FOUND));

    // 현재 비밀번호 확인
    if (!bCryptpasswordEncoder.matches(request.currentPassword(), user.getPassword())) {
      throw new CustomException(UserException.ERROR_PASSWORD);
    }

    // 새 비밀번호로 변경
    user.setPassword(bCryptpasswordEncoder.encode(request.newPassword()));
    userRepository.save(user);

    return "비밀번호가 변경되었습니다.";
  }

  public String signup(SignupRequest request) {
    if (userRepository.existsByUserId(request.userId())) {
      throw new CustomException(UserException.USER_ALREADY_EXISTS);
    } else if (userRepository.existsByEmail(request.email())) {
      throw new CustomException(UserException.EMAIL_ALREADY_EXISTS);
    } else if (request.studentNo() != null
        && userRepository.existsByStudentNo(request.studentNo())) {
      throw new CustomException(UserException.STUDENT_ALREADY_EXISTS);
    } else {

      // 비밀번호를 BCrypt로 인코딩
      String encodedPassword = bCryptpasswordEncoder.encode(request.password());

      Users user = Users.builder().build();
      user.setUserId(request.userId());
      user.setPassword(encodedPassword);
      user.setEmail(request.email());
      user.setUserName(request.userName());
      user.setStudentNo(request.studentNo());

      user.setDefaults();

      userRepository.save(user);

      //      // 트랜잭션 유저히스토리관리 추가
      //        @Transactional
      //        UsersHistory usersHistory =
    }
    return "회원가입 성공";
  }

  public SigninResponse signin(
      SigninRequest request, String ip, String userAgent, HttpServletResponse httpResponse) {
    Users user =
        userRepository
            .findByUserId(request.userId())
            .orElseThrow(() -> new CustomException(UserException.USER_NOT_FOUND));

    if (!bCryptpasswordEncoder.matches(request.password(), user.getPassword())) {
      throw new CustomException(UserException.ERROR_PASSWORD);
    }

    // 기존 리프레시 토큰 확인
    Optional<RefreshToken> existingTokenOpt = refreshTokenRepository.findByUsers(user);
    RefreshToken token = null;
    if (existingTokenOpt.isPresent()) {
      token = existingTokenOpt.get();

      boolean isExpired = token.getExpiredAt().isBefore(LocalDateTime.now());
      boolean isDifferentIpOrAgent =
          !token.getIp().equals(ip) || !token.getUserAgent().equals(userAgent);

      if (isExpired) {
        // 만료되면 삭제하고 새로 발급
        refreshTokenRepository.delete(token);
      } else if (isDifferentIpOrAgent) {
        // IP 또는 UserAgent가 다르면 새로 발급
        // 기존 토큰 삭제하지 않음 (필요시 정책적으로 삭제 가능)
      } else {
        // 만료되지 않았고 IP/UserAgent가 같으면 기존 토큰 사용
        String accessToken = jwtUtil.createAccessToken(user);
        return new SigninResponse(user.getUserNo(), accessToken);
      }
    }
    String accessToken = jwtUtil.createAccessToken(user);
    String refreshToken = jwtUtil.createRefreshToken(user);

    // RefreshToken 엔티티 저장
    RefreshToken tokenEntity = RefreshToken.builder().build();
    tokenEntity.setUsers(user);
    tokenEntity.setRefreshToken(refreshToken);
    tokenEntity.setExpiredAt(LocalDateTime.now().plusDays(7)); // 만료 기간 설정
    tokenEntity.setIp(ip);
    tokenEntity.setUserAgent(userAgent);

    refreshTokenRepository.save(tokenEntity);

    jwtUtil.addRefreshTokenToCookie(httpResponse, tokenEntity.getRefreshToken());

    return new SigninResponse(user.getUserNo(), accessToken);
  }

  public TokenResponse newAccessToken(String ip, String userAgent) {
    RefreshToken savedToken =
        refreshTokenRepository
            .findByIpAndUserAgent(ip, userAgent)
            .orElseThrow(() -> new CustomException(UserException.INVALID_REFRESH_TOKEN));

    // 리프레시 토큰 만료 확인
    if (savedToken.getExpiredAt().isBefore(LocalDateTime.now())) {
      throw new CustomException(UserException.FORBIDDEN); // 프론트에서 로그인 화면으로 유도
    }

    Users user =
        userRepository
            .findByUserNo(savedToken.getUsers().getUserNo())
            .orElseThrow(() -> new CustomException(UserException.USER_NOT_FOUND));

    String newAccessToken = jwtUtil.createAccessToken(user);
    return new TokenResponse(newAccessToken);
  }

  public String logout(String ip, String userAgent, String refreshToken) {
    // 토큰으로 엔티티 조회
    RefreshToken token =
        refreshTokenRepository
            .findByIpAndUserAgentAndRefreshToken(ip, userAgent, refreshToken)
            .orElseThrow(() -> new CustomException(UserException.INVALID_REFRESH_TOKEN));
    // 로그아웃시 토큰삭제
    refreshTokenRepository.delete(token);
    return "로그아웃 성공";
  }
}

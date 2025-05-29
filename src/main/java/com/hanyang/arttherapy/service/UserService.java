package com.hanyang.arttherapy.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanyang.arttherapy.common.exception.CustomException;
import com.hanyang.arttherapy.common.exception.exceptionType.UserException;
import com.hanyang.arttherapy.common.util.JwtUtil;
import com.hanyang.arttherapy.domain.RefreshToken;
import com.hanyang.arttherapy.domain.Users;
import com.hanyang.arttherapy.domain.UsersHistory;
import com.hanyang.arttherapy.dto.request.userRequest.*;
import com.hanyang.arttherapy.dto.response.userResponse.*;
import com.hanyang.arttherapy.repository.RefreshTokenRepository;
import com.hanyang.arttherapy.repository.UserRepository;
import com.hanyang.arttherapy.repository.UsersHistoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
  @Autowired private HttpSession session;
  private final UserRepository userRepository;
  private final UsersHistoryRepository usersHistoryRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final JwtUtil jwtUtil;
  private final BCryptPasswordEncoder bCryptpasswordEncoder;

  private static final long VERIFICATION_CODE_EXPIRATION_TIME = 3 * 60 * 1000;

  public boolean existsByUserId(String userId) {
    return userRepository.existsByUserId(userId);
  }

  public boolean existsByStudentNo(String studentNo) {
    return userRepository.existsByStudentNo(studentNo);
  }

  public String checkEmail(EmailRequest request) {
    if (userRepository.existsByEmail(request.email())) {
      return "이미 사용 중인 이메일입니다.";
    }

    String verificationCode = generateTemporaryPassword();
    sendEmailVerification(request.email(), verificationCode);

    return "이메일이 발송되었습니다. 인증번호를 확인해주세요";
  }

  private void sendEmailVerification(String email, String verificationCode) {
    // 실제 이메일 발송 제거 → 콘솔에 출력
    System.out.println("이메일 인증 코드 [" + email + "] : " + verificationCode);

    long expirationTime = System.currentTimeMillis() + VERIFICATION_CODE_EXPIRATION_TIME;
    session.setAttribute("verificationCode", verificationCode);
    session.setAttribute("verificationCodeExpirationTime", expirationTime);
  }

  public String verifyEmailCode(VerificationRequest request) {
    String storedCode = (String) session.getAttribute("verificationCode");
    Long expirationTime = (Long) session.getAttribute("verificationCodeExpirationTime");

    if (storedCode != null && expirationTime != null) {
      if (System.currentTimeMillis() > expirationTime) {
        return "인증 번호가 만료되었습니다.";
      }
      if (storedCode.equals(request.verificationCode())) {
        return "이메일 인증이 완료되었습니다.";
      } else {
        return "인증 번호가 일치하지 않습니다.";
      }
    }
    return "인증되었습니다.";
  }

  public String findByEmailAndUserName(IdRequest request) {
    Users user =
        userRepository
            .findByEmailAndUserName(request.email(), request.userName())
            .orElseThrow(() -> new CustomException(UserException.USER_NOT_FOUND));

    return maskUserId(user.getUserId());
  }

  private String maskUserId(String originalId) {
    if (originalId.length() <= 4) {
      return originalId;
    }
    return originalId.substring(0, 4) + "*".repeat(originalId.length() - 4);
  }

  public String newPassword(String userId, String email) {
    Optional<Users> userOpt = userRepository.findByUserIdAndEmail(userId, email);
    if (userOpt.isEmpty()) {
      throw new CustomException(UserException.USER_NOT_FOUND);
    }

    Users user = userOpt.get();
    String temporaryPassword = generateTemporaryPassword();
    user.setPassword(bCryptpasswordEncoder.encode(temporaryPassword));
    userRepository.save(user);

    sendTemporaryPasswordEmail(email, temporaryPassword);
    return "임시 비밀번호가 이메일로 전송되었습니다.";
  }

  private String generateTemporaryPassword() {
    int length = 9;
    String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~!@#$%^&*";
    Random random = new Random();
    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      sb.append(chars.charAt(random.nextInt(chars.length())));
    }
    sb.append('@');
    return sb.toString();
  }

  private String sendTemporaryPasswordEmail(String email, String temporaryPassword) {
    System.out.println("임시 비밀번호 [" + email + "] : " + temporaryPassword);
    return "임시 비밀번호가 이메일로 발송되었습니다.";
  }

  public String resetPassword(PasswordResetRequest request) {
    Users user =
        userRepository
            .findByUserId(request.userId())
            .orElseThrow(() -> new CustomException(UserException.USER_NOT_FOUND));

    if (!bCryptpasswordEncoder.matches(request.currentPassword(), user.getPassword())) {
      throw new CustomException(UserException.ERROR_PASSWORD);
    }

    user.setPassword(bCryptpasswordEncoder.encode(request.newPassword()));
    userRepository.save(user);
    return "비밀번호가 변경되었습니다.";
  }

  @Transactional
  public void signup(SignupRequest request) {
    if (userRepository.existsByUserId(request.userId())) {
      throw new CustomException(UserException.USER_ALREADY_EXISTS);
    } else if (userRepository.existsByEmail(request.email())) {
      throw new CustomException(UserException.EMAIL_ALREADY_EXISTS);
    } else if (request.studentNo() != null
        && userRepository.existsByStudentNo(request.studentNo())) {
      throw new CustomException(UserException.STUDENT_ALREADY_EXISTS);
    }

    String encodedPassword = bCryptpasswordEncoder.encode(request.password());

    Users user =
        Users.builder()
            .userId(request.userId())
            .password(encodedPassword)
            .email(request.email())
            .userName(request.userName())
            .studentNo(request.studentNo())
            .build();

    user.setDefaults();
    userRepository.save(user);

    UsersHistory history = new UsersHistory();
    history.setUser(user);
    history.setSigninTimestamp(new Timestamp(System.currentTimeMillis()));
    usersHistoryRepository.save(history);
  }

  @Transactional
  public SigninResponse signin(
      SigninRequest request, String ip, String userAgent, HttpServletResponse httpResponse) {
    Users user =
        userRepository
            .findByUserId(request.userId())
            .orElseThrow(() -> new CustomException(UserException.USER_NOT_FOUND));

    if (!bCryptpasswordEncoder.matches(request.password(), user.getPassword())) {
      throw new CustomException(UserException.ERROR_PASSWORD);
    }

    Optional<RefreshToken> existingTokenOpt = refreshTokenRepository.findByUsers(user);
    RefreshToken token = null;

    if (existingTokenOpt.isPresent()) {
      token = existingTokenOpt.get();
      boolean isExpired = token.getExpiredAt().isBefore(LocalDateTime.now());
      boolean isDifferentIpOrAgent =
          !token.getIp().equals(ip) || !token.getUserAgent().equals(userAgent);

      if (isExpired) {
        refreshTokenRepository.delete(token);
      } else if (!isDifferentIpOrAgent) {
        String accessToken = jwtUtil.createAccessToken(user);
        jwtUtil.addRefreshTokenToCookie(httpResponse, token.getRefreshToken());
        return new SigninResponse(user.getUserNo(), accessToken, user.getRole());
      }
    }

    String accessToken = jwtUtil.createAccessToken(user);
    String refreshToken = jwtUtil.createRefreshToken(user);

    RefreshToken tokenEntity =
        RefreshToken.builder()
            .users(user)
            .refreshToken(refreshToken)
            .expiredAt(LocalDateTime.now().plusDays(7))
            .ip(ip)
            .userAgent(userAgent)
            .build();

    refreshTokenRepository.save(tokenEntity);
    jwtUtil.addRefreshTokenToCookie(httpResponse, tokenEntity.getRefreshToken());
    httpResponse.setHeader("Authorization", "Bearer " + accessToken);
    return new SigninResponse(user.getUserNo(), accessToken, user.getRole());
  }

  public TokenResponse newAccessToken(String ip, String userAgent) {
    RefreshToken savedToken =
        refreshTokenRepository
            .findByIpAndUserAgent(ip, userAgent)
            .orElseThrow(() -> new CustomException(UserException.INVALID_REFRESH_TOKEN));

    if (savedToken.getExpiredAt().isBefore(LocalDateTime.now())) {
      throw new CustomException(UserException.FORBIDDEN);
    }

    Users user =
        userRepository
            .findByUserNo(savedToken.getUsers().getUserNo())
            .orElseThrow(() -> new CustomException(UserException.USER_NOT_FOUND));

    String newAccessToken = jwtUtil.createAccessToken(user);
    return new TokenResponse(newAccessToken);
  }

  public String logout(String ip, String userAgent, String refreshToken) {
    RefreshToken token =
        refreshTokenRepository
            .findByIpAndUserAgentAndRefreshToken(ip, userAgent, refreshToken)
            .orElseThrow(() -> new CustomException(UserException.INVALID_REFRESH_TOKEN));

    refreshTokenRepository.delete(token);
    return "로그아웃 되었습니다.";
  }
}

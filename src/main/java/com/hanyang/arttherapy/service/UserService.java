package com.hanyang.arttherapy.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanyang.arttherapy.common.exception.CustomException;
import com.hanyang.arttherapy.common.exception.exceptionType.UserException;
import com.hanyang.arttherapy.common.util.JwtUtil;
import com.hanyang.arttherapy.domain.RefreshToken;
import com.hanyang.arttherapy.domain.Users;
import com.hanyang.arttherapy.domain.UsersHistory;
import com.hanyang.arttherapy.domain.enums.UserStatus;
import com.hanyang.arttherapy.dto.request.MypageEmailRequest;
import com.hanyang.arttherapy.dto.request.users.*;
import com.hanyang.arttherapy.dto.response.userResponse.SigninResponse;
import com.hanyang.arttherapy.dto.response.userResponse.TokenResponse;
import com.hanyang.arttherapy.repository.RefreshTokenRepository;
import com.hanyang.arttherapy.repository.UserRepository;
import com.hanyang.arttherapy.repository.UsersHistoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
  private static final long VERIFICATION_CODE_EXPIRATION_TIME = 3 * 60 * 1000; // 3분
  private static final int MAX_RETRY = 10;
  private final UserRepository userRepository;
  private final UsersHistoryRepository usersHistoryRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final JwtUtil jwtUtil;
  private final BCryptPasswordEncoder bCryptpasswordEncoder;
  @Autowired private HttpSession session;
  @Autowired private JavaMailSender mailSender;

  public boolean existsByUserId(String userId) {
    return userRepository.existsByUserId(userId);
  }

  public boolean existsByStudentNo(String studentNo) {
    return userRepository.existsByStudentNo(studentNo);
  }

  public String checkEmail(EmailRequest request) {

    // 이메일이 이미 존재하는지 확인
    if (userRepository.existsByEmail(request.email())) {
      throw new CustomException(UserException.EMAIL_ALREADY_EXISTS);
    }
    // 인증번호 생성
    String verificationCode = generateTemporaryPassword();

    // 이메일 발송 및 세션 저장
    sendEmailVerification(request.email(), verificationCode);

    return "이메일이 발송되었습니다. 인증번호를 확인해주세요";
  }

  // 마이페이지 이메일변경용 체크
  public String checkEmailForChange(MypageEmailRequest request) {
    Optional<Users> existingUser = userRepository.findByEmail(request.email());

    // 이미 존재하는데, 내 계정이 아니라면 중복
    if (existingUser.isPresent() && !existingUser.get().getUserNo().equals(request.userNo())) {
      throw new CustomException(UserException.EMAIL_ALREADY_EXISTS);
    }

    // 본인이 사용 중이거나 새로운 이메일인 경우 인증 절차 진행
    String verificationCode = generateTemporaryPassword();
    sendEmailVerification(request.email(), verificationCode);

    return "이메일이 발송되었습니다. 인증번호를 확인해주세요";
  }

  // 이메일 발송 + 세션 저장
  private void sendEmailVerification(String email, String verificationCode) {
    // 이메일 발송
    try {
      SimpleMailMessage message = new SimpleMailMessage();
      message.setTo(email);
      message.setSubject("이메일 설정 인증번호");
      message.setText("안녕하세요. 인증 번호는   " + verificationCode + "   입니다.\n인증시 공백이 들어가지 않도록 주의해주세요.");
      message.setFrom("mingke48@gmail.com");
      mailSender.send(message);
    } catch (Exception e) {
      throw new CustomException(UserException.EMAIL_SEND_FAIL);
    }

    // 세션 저장
    long expirationTime = System.currentTimeMillis() + VERIFICATION_CODE_EXPIRATION_TIME;
    session.setAttribute("verificationCode", verificationCode);
    session.setAttribute("verificationCodeExpirationTime", expirationTime);
  }

  // 인증 번호 검증
  public String verifyEmailCode(VerificationRequest request) {
    String storedCode = (String) session.getAttribute("verificationCode");
    Long expirationTime = (Long) session.getAttribute("verificationCodeExpirationTime");

    if (storedCode == null || expirationTime == null) {
      throw new CustomException(UserException.VERIFICATION_CODE_NOT_FOUND);
    }

    if (System.currentTimeMillis() > expirationTime) {
      throw new CustomException(UserException.VERIFICATION_CODE_EXPIRED);
    }

    if (!storedCode.equals(request.verificationCode())) {
      throw new CustomException(UserException.VERIFICATION_CODE_MISMATCH);
    }

    return "이메일 인증이 완료되었습니다.";
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
    if (originalId == null || originalId.length() <= 3) {
      return originalId;
    }

    if (originalId.contains("@")) {
      // 이메일 형식 처리
      int atIndex = originalId.indexOf("@");
      String localPart = originalId.substring(0, atIndex);
      String domainPart = originalId.substring(atIndex); // @부터 끝까지

      if (localPart.length() <= 3) {
        return localPart + domainPart; // 3자리 이하는 그대로
      }

      return localPart.substring(0, 3) + "*".repeat(localPart.length() - 3) + domainPart;
    } else {
      // 일반 아이디 처리
      return originalId.substring(0, 3) + "*".repeat(originalId.length() - 3);
    }
  }

  // 비밀번호 찾기
  public String newPassword(String userId, String email) {
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

  // 임시 비밀번호 생성 (예: 9자리 랜덤+마지막 고정'@')
  private String generateTemporaryPassword() {
    int length = 9;
    String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~!@#$%^&*";
    Random random = new Random();
    StringBuilder sb = new StringBuilder(length);

    for (int i = 0; i < length; i++) {
      int index = random.nextInt(chars.length());
      sb.append(chars.charAt(index));
    }
    sb.append('@'); // 마지막 고정 문자 추가
    return sb.toString();
  }

  // 이메일로 임시 비밀번호 보내기
  private String sendTemporaryPasswordEmail(String email, String temporaryPassword) {
    try {
      SimpleMailMessage message = new SimpleMailMessage();
      message.setTo(email);
      message.setSubject("임시 비밀번호 안내");
      message.setText(
          "안녕하세요. 임시 비밀번호는   " + temporaryPassword + "   입니다.\n인증시 공백이 들어가지 않도록 주의해주세요.");
      message.setFrom("mingke48@gmail.com");
      mailSender.send(message);
    } catch (Exception e) {
      throw new CustomException(UserException.EMAIL_SEND_FAIL);
    }
    return "임시 비밀번호가 이메일로 발송되었습니다.";
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

  @Transactional
  public void signup(SignupRequest request) {
    // 필수 입력값 검증
    if (request.userId() == null || request.userId().isBlank()) {
      throw new CustomException(UserException.BLANK_REQUIRED);
    }

    if (request.password() == null || request.password().isBlank()) {
      throw new CustomException(UserException.BLANK_REQUIRED);
    }

    if (request.email() == null || request.email().isBlank()) {
      throw new CustomException(UserException.BLANK_REQUIRED);
    }

    if (userRepository.existsByUserId(request.userId())) {
      throw new CustomException(UserException.USER_ALREADY_EXISTS);
    } else if (userRepository.existsByEmail(request.email())) {
      throw new CustomException(UserException.EMAIL_ALREADY_EXISTS);
    } else if (request.studentNo() != null
        && userRepository.existsByStudentNo(request.studentNo())) {
      throw new CustomException(UserException.STUDENT_ALREADY_EXISTS);
    } else {

      // 중복되지 않는 랜덤 userNo 생성
      Long userNo = (long) generateUniqueUserNo();

      // 비밀번호를 BCrypt로 인코딩
      String encodedPassword = bCryptpasswordEncoder.encode(request.password());

      Users user =
          Users.builder()
              .userNo(userNo)
              .userId(request.userId())
              .password(encodedPassword)
              .email(request.email())
              .userName(request.userName())
              .studentNo(request.studentNo())
              .build();

      user.setDefaults();

      userRepository.save(user);

      // UsersHistory 생성 및 저장
      UsersHistory history = new UsersHistory();
      history.setUser(user);
      Timestamp now = new Timestamp(System.currentTimeMillis());
      history.setSigninTimestamp(now);

      usersHistoryRepository.save(history); // UsersHistory 저장
    }
  }

  private int generateUniqueUserNo() {
    Random random = new Random();
    int attempts = 0;
    Long userNo;
    do {
      userNo = random.nextLong() & Long.MAX_VALUE; // 양수만 생성
      attempts++;
      if (attempts > MAX_RETRY) {
        throw new CustomException(UserException.USER_NO_FAILED);
      }
    } while (userRepository.existsByUserNo(userNo));
    return Math.toIntExact(userNo);
  }

  @Transactional
  public SigninResponse signin(
      SigninRequest request, String ip, String userAgent, HttpServletResponse httpResponse) {
    Users user =
        userRepository
            .findByUserId(request.userId())
            .orElseThrow(() -> new CustomException(UserException.USER_NOT_FOUND));

    // 사용자 상태 UNACTIVE 로그인 불가
    if (user.getUserStatus() == UserStatus.UNACTIVE) {
      throw new CustomException(UserException.USER_STATUS_UNACTIVE); // 탈퇴회원
    }

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
        jwtUtil.addRefreshTokenToCookie(httpResponse, token.getRefreshToken());
        return new SigninResponse(user.getUserNo(), accessToken, user.getRole());
      }
    }
    String accessToken = jwtUtil.createAccessToken(user);
    String refreshToken = jwtUtil.createRefreshToken(user);

    // RefreshToken 엔티티 저장
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
    return "로그아웃 되었습니다.";
  }
}

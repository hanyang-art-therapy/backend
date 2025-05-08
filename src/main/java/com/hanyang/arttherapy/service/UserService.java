package com.hanyang.arttherapy.service;

import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.hanyang.arttherapy.common.exception.CustomException;
import com.hanyang.arttherapy.common.exception.exceptionType.UserException;
import com.hanyang.arttherapy.domain.Users;
import com.hanyang.arttherapy.dto.request.userRequest.IdRequest;
import com.hanyang.arttherapy.dto.request.userRequest.PasswordResetRequest;
import com.hanyang.arttherapy.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  @Autowired private JavaMailSender mailSender;

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
    System.out.println(">>> newPassword called with userId: " + userId + ", email: " + email);
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
    String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
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
}

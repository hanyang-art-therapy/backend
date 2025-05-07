package com.hanyang.arttherapy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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
}

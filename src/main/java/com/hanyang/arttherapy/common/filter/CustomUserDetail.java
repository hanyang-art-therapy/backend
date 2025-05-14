package com.hanyang.arttherapy.common.filter;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.hanyang.arttherapy.domain.Users;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CustomUserDetail implements UserDetails {

  private final Users user;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of((GrantedAuthority) () -> "ROLE_" + user.getRole().name());
  }

  @Override
  public String getPassword() {
    return user.getPassword();
  }

  @Override
  public String getUsername() {
    return user.getUserId(); // 로그인 ID
  }

  @Override
  public boolean isAccountNonExpired() {
    return true; // 계정 만료 여부, 필요 시 설정
  }

  @Override
  public boolean isAccountNonLocked() {
    return true; // 계정 잠금 여부
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true; // 비밀번호 만료 여부
  }

  @Override
  public boolean isEnabled() {
    return user.getUserStatus() == com.hanyang.arttherapy.domain.enums.UserStatus.ACTIVE;
  }

  public Users getUser() {
    return user;
  }
}

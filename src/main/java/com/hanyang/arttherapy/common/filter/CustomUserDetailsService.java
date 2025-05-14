package com.hanyang.arttherapy.common.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.hanyang.arttherapy.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
  @Autowired private UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository
        .findByUserId(username)
        .map(CustomUserDetail::new)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
  }
}

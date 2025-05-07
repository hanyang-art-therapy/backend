package com.hanyang.arttherapy.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetRequest {
  private String userId;
  private String email;
}

package com.hanyang.arttherapy.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponseDto {
  private Long reviewNo;
  private Long artsNo;
  private String reviewText;
  private LocalDateTime createdAt;
  private String userName;
  private List<FileResponseDto> files;
}

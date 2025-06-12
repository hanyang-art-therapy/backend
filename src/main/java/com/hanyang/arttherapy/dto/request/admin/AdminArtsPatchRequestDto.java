package com.hanyang.arttherapy.dto.request.admin;

import java.util.List;

import com.hanyang.arttherapy.domain.enums.ArtType;

import lombok.Data;

@Data
public class AdminArtsPatchRequestDto {

  private String artName; // 작품명 (nullable)
  private String caption; // 캡션 (nullable)
  private ArtType artType; // SINGLE / GROUP (nullable)
  private Long filesNo; // 파일 ID (nullable)
  private Long galleriesNo; // 전시회 ID (nullable)
  private String coDescription; // 공동작품 설명 (nullable)

  private List<ArtistInfo> artists; // 작가 리스트 (nullable)

  @Data
  public static class ArtistInfo {
    private Long artistNo; // 작가 번호
    private String description; // 작가별 설명
  }
}

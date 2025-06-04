package com.hanyang.arttherapy.dto.request.admin;

import java.util.List;

import com.hanyang.arttherapy.domain.enums.ArtType;

import lombok.Data;

@Data
public class AdminArtsRequestDto {

  private String artName; // 작품명
  private String caption; // 설명
  private ArtType artType; // SINGLE / GROUP
  private Long filesNo; // 연결된 이미지 파일 번호
  private Long galleriesNo; // 연결된 전시회 번호
  private String coDescription; // 공통작품 설명

  private List<ArtistInfo> artistList; // 작가 리스트

  @Data
  public static class ArtistInfo {
    private Long artistNo; // 작가 번호
    private String description; // 작가별 설명
  }
}

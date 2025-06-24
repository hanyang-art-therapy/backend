package com.hanyang.arttherapy.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.hanyang.arttherapy.domain.enums.NoticeCategory;
import com.hanyang.arttherapy.dto.request.NoticeRequestDto;
import com.hanyang.arttherapy.dto.response.noticeResponse.CommonDataResponse;
import com.hanyang.arttherapy.dto.response.noticeResponse.NoticeDetailResponseDto;
import com.hanyang.arttherapy.dto.response.noticeResponse.NoticeListResponseDto;
import com.hanyang.arttherapy.dto.response.userResponse.CommonMessageResponse;
import com.hanyang.arttherapy.service.NoticesService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notices")
public class NoticesController {

  private final NoticesService noticesService;

  // 전체 조회
  @GetMapping
  public NoticeListResponseDto getNotices(
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) NoticeCategory category,
      @RequestParam(defaultValue = "0") int page) {
    return noticesService.getNotices(keyword, category, page);
  }

  // 상세 조회
  @GetMapping("/{noticeNo}")
  public NoticeDetailResponseDto getNoticeDetail(@PathVariable Long noticeNo) {
    return noticesService.getNoticeDetail(noticeNo);
  }

  // 게시글 등록
  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<CommonDataResponse<NoticeDetailResponseDto>> createNotice(
      @RequestBody @Valid NoticeRequestDto requestDto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(noticesService.createNotice(requestDto));
  }

  // 게시글 수정
  @PatchMapping("/{noticeNo}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<CommonDataResponse<NoticeDetailResponseDto>> updateNotice(
      @PathVariable Long noticeNo, @RequestBody @Valid NoticeRequestDto requestDto) {
    return ResponseEntity.ok(noticesService.updateNotice(noticeNo, requestDto));
  }

  // 게시글 삭제
  @DeleteMapping("/{noticeNo}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<CommonMessageResponse> deleteNotice(@PathVariable Long noticeNo) {
    CommonMessageResponse response = noticesService.deleteNotice(noticeNo);
    return ResponseEntity.ok(response);
  }
}

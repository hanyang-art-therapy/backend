package com.hanyang.arttherapy.dto.response;

import java.util.List;

/**
 * 공통 무한스크롤 응답 DTO
 *
 * @param <T> content에 담기는 요소의 타입
 */
public record CommonScrollResponse<T>(List<T> content, Long lastId, boolean hasNext) {}

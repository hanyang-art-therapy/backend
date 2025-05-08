package com.hanyang.arttherapy.service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.hanyang.arttherapy.domain.ArtArtistRel;
import com.hanyang.arttherapy.domain.Arts;
import com.hanyang.arttherapy.domain.Files;
import com.hanyang.arttherapy.dto.response.*;
import com.hanyang.arttherapy.repository.ArtsRepository;
import com.hanyang.arttherapy.repository.FilesRepository;

@Service
public class ArtsService {

  private final ArtsRepository artsRepository;
  private final FilesRepository filesRepository;
  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
  private final ReviewService reviewService;

  public ArtsService(
      ArtsRepository artsRepository, FilesRepository filesRepository, ReviewService reviewService) {
    this.artsRepository = artsRepository;
    this.filesRepository = filesRepository;
    this.reviewService = reviewService;
  }

  public ArtsDetailResponseDto getArtDetail(Long galleriesNo, Long artsNo) {
    Arts arts =
        artsRepository
            .findArtDetailById(artsNo)
            .orElseThrow(() -> new EntityNotFoundException("해당 작품을 찾을 수 없습니다."));

    ArtsDetailResponseDto response = new ArtsDetailResponseDto();
    response.setArtsNo(arts.getArtsNo());
    response.setArtName(arts.getArtName());
    response.setCaption(arts.getCaption());

    response.setDescription(arts.getArtArtistRelList().get(0).getDescription());

    response.setUploadedAt(
        Optional.ofNullable(arts.getUploadedAt()).map(date -> date.format(formatter)).orElse(null));

    Optional.ofNullable(arts.getGalleries())
        .ifPresent(
            gallery -> {
              GallerySimpleDto galleryDto = new GallerySimpleDto();
              galleryDto.setGalleriesNo(gallery.getGalleriesNo());
              response.setGalleries(galleryDto);
            });

    List<String> descriptions =
        arts.getArtArtistRelList().stream()
            .map(ArtArtistRel::getDescription)
            .collect(Collectors.toList());

    response.setDescription(descriptions.isEmpty() ? "작품 설명이 없습니다." : descriptions.get(0));

    List<ArtistResponseDto> artistDtos =
        arts.getArtArtistRelList().stream()
            .map(artArtistRel -> ArtistResponseDto.of(artArtistRel.getArtist()))
            .collect(Collectors.toList());

    response.setArtists(artistDtos);

    List<Files> fileDetails =
        filesRepository.findByFilesNoInAndUseYn(
            artsRepository.findFileIdsByArtsNo(arts.getArtsNo()), true);

    List<FileResponseDto> fileDtos =
        fileDetails.stream()
            .map(
                file ->
                    new FileResponseDto(
                        file.getFilesNo(),
                        file.getName(),
                        file.getUrl(),
                        file.getFilesSize(),
                        file.getExtension(),
                        file.getFilesType()))
            .collect(Collectors.toList());
    response.setFiles(fileDtos);

    Page<ReviewResponseDto> reviewPage = reviewService.getReviews(artsNo, 0, 5);
    response.setReviews(reviewPage.getContent());

    return response;
  }
}

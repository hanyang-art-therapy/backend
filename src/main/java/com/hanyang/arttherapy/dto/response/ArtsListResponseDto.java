// package com.hanyang.arttherapy.dto.response;
//
// import java.util.List;
// import java.util.stream.Collectors;
//
// import com.hanyang.arttherapy.domain.ArtArtistRel;
// import com.hanyang.arttherapy.domain.Arts;
// import com.hanyang.arttherapy.domain.Files;
//
// import lombok.Builder;
//
// public record ArtsListResponseDto(
//    Long artsNo,
//    String artName,
//    FileResponse file,
//    List<ArtistResponse> artists,
//    Long galleriesNo) {
//
//  @Builder
//  public static ArtsListResponseDto of(
//      Arts arts, Files file, List<ArtArtistRel> artArtistRels, Long galleriesNo) {
//    return new ArtsListResponseDto(
//        arts.getArtsNo(),
//        arts.getArtName(),
//        new FileResponse(file.getUrl()),
//        artArtistRels.stream()
//            .map(
//                rel ->
//                    new ArtistResponse(
//                        rel.getArtists().getArtistName(), rel.getArtists().getCohort()))
//            .collect(Collectors.toList()),
//        galleriesNo);
//  }
//
//  public record FileResponse(String url) {}
//
//  public record ArtistResponse(String artistName, int cohort) {}
// }

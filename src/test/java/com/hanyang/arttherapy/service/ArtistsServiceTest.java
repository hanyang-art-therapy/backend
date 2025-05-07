package com.hanyang.arttherapy.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.hanyang.arttherapy.common.exception.*;
import com.hanyang.arttherapy.domain.*;
import com.hanyang.arttherapy.dto.request.*;
import com.hanyang.arttherapy.dto.response.*;
import com.hanyang.arttherapy.repository.*;

@SpringBootTest
class ArtistsServiceTest {

  @Autowired private ArtistsService artistsService;
  @Autowired private ArtistsRepository artistsRepository;

  private ArtistRequestDto artistRequestDto;
  private Artists savedArtist;

  @BeforeEach
  void init() {
    artistRequestDto = new ArtistRequestDto("이한양", "2020101010", 20);
  }

  @Test
  @Transactional
  void registerArtist() {
    artistsService.isStudentNoDuplicate(artistRequestDto.studentNo());
    artistsService.registerArtist(artistRequestDto);
    savedArtist = artistsService.findByStudentNo(artistRequestDto.studentNo());

    assertThat(savedArtist.getArtistsNo()).isNotNull();
    assertThat(savedArtist.getArtistName()).isEqualTo("이한양");
    assertThat(savedArtist.getStudentNo()).isEqualTo("2020101010");
    assertThat(savedArtist.getCohort()).isEqualTo(20);
  }

  @Test
  void getArtist() {
    artistsService.registerArtist(artistRequestDto);
    savedArtist = artistsService.findByStudentNo(artistRequestDto.studentNo());

    ArtistResponseDto response = artistsService.getArtist(savedArtist.getArtistsNo());

    assertThat(response).isNotNull();
    assertThat(response.artistName()).isEqualTo(savedArtist.getArtistName());
    assertThat(response.studentNo()).isEqualTo(savedArtist.getStudentNo());
    assertThat(response.cohort()).isEqualTo(savedArtist.getCohort());
  }

  @Test
  void updateArtist() {
    artistsService.registerArtist(artistRequestDto);
    savedArtist = artistsService.findByStudentNo(artistRequestDto.studentNo());

    ArtistRequestDto updatedDto = new ArtistRequestDto("김한양", "2015151515", 25);

    artistsService.updateArtist(savedArtist.getArtistsNo(), updatedDto);

    savedArtist = artistsService.findByStudentNo(updatedDto.studentNo());

    assertThat(savedArtist.getArtistName()).isEqualTo(updatedDto.artistName());
    assertThat(savedArtist.getCohort()).isEqualTo(updatedDto.cohort());
    assertThat(savedArtist.getStudentNo()).isEqualTo(updatedDto.studentNo());
  }

  @Test
  void deleteArtist() {
    artistsService.registerArtist(artistRequestDto);
    savedArtist = artistsService.findByStudentNo(artistRequestDto.studentNo());

    artistsService.deleteAritst(savedArtist.getArtistsNo());

    Assertions.assertThrows(
        CustomException.class,
        () -> {
          artistsService.findByStudentNo(artistRequestDto.studentNo());
        });
  }
}

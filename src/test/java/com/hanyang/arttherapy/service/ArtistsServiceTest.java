//package com.hanyang.arttherapy.service;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.hanyang.arttherapy.common.exception.CustomException;
//import com.hanyang.arttherapy.domain.Artists;
//import com.hanyang.arttherapy.dto.request.ArtistRequestDto;
//import com.hanyang.arttherapy.dto.response.artistResponse.ArtistResponseDto;
//import com.hanyang.arttherapy.repository.ArtistsRepository;
//
//@SpringBootTest
//@Transactional
//class ArtistsServiceTest {
//
//    @Autowired
//    private ArtistsService artistsService;
//
//    @Autowired
//    private ArtistsRepository artistsRepository;
//
//    private ArtistRequestDto artistRequestDto;
//
//    @BeforeEach
//    void setUp() {
//        artistRequestDto = new ArtistRequestDto("이한양", "2020101012", 20);
//    }
//
//    @Test
//    void registerArtist() {
//        // 중복 체크는 registerArtist 내부에서 이미 하는 게 보통이므로 따로 호출 안 해도 됨
//        String result = artistsService.registerArtist(artistRequestDto);
//        Artists savedArtist = artistsService.findByStudentNo(artistRequestDto.studentNo());
//
//        assertThat(result).isEqualTo("작가등록 성공");
//        assertThat(savedArtist.getArtistsNo()).isNotNull();
//        assertThat(savedArtist.getArtistName()).isEqualTo("이한양");
//        assertThat(savedArtist.getStudentNo()).isEqualTo("2020101012");
//        assertThat(savedArtist.getCohort()).isEqualTo(20);
//    }
//
//    @Test
//    void getArtist() {
//        artistsService.registerArtist(artistRequestDto);
//        Artists savedArtist = artistsService.findByStudentNo(artistRequestDto.studentNo());
//
//        ArtistResponseDto response = artistsService.getArtist(savedArtist.getArtistsNo());
//
//        assertThat(response).isNotNull();
//        assertThat(response.artistName()).isEqualTo(savedArtist.getArtistName());
//        assertThat(response.studentNo()).isEqualTo(savedArtist.getStudentNo());
//        assertThat(response.cohort()).isEqualTo(savedArtist.getCohort());
//    }
//
//    @Test
//    void updateArtist() {
//        artistsService.registerArtist(artistRequestDto);
//        Artists savedArtist = artistsService.findByStudentNo(artistRequestDto.studentNo());
//
//        ArtistRequestDto updatedDto = new ArtistRequestDto("김한양", "2015151515", 25);
//
//        String updateResult = artistsService.updateArtist(savedArtist.getArtistsNo(), updatedDto);
//        Artists updatedArtist = artistsService.findByStudentNo(updatedDto.studentNo());
//
//        assertThat(updateResult).isEqualTo("작가 수정이 완료되었습니다");
//        assertThat(updatedArtist.getArtistName()).isEqualTo(updatedDto.artistName());
//        assertThat(updatedArtist.getStudentNo()).isEqualTo(updatedDto.studentNo());
//        assertThat(updatedArtist.getCohort()).isEqualTo(updatedDto.cohort());
//    }
//
//    @Test
//    void deleteArtist() {
//        artistsService.registerArtist(artistRequestDto);
//        Artists savedArtist = artistsService.findByStudentNo(artistRequestDto.studentNo());
//
//        String deleteResult = artistsService.deleteArtist(savedArtist.getArtistsNo());
//        assertThat(deleteResult).isEqualTo("작가 삭제가 완료되었습니다.");
//
//        Assertions.assertThrows(
//                CustomException.class,
//                () -> artistsService.findByStudentNo(artistRequestDto.studentNo())
//        );
//    }
//}

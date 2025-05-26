package com.hanyang.arttherapy.domain;

import jakarta.persistence.*;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "professors")
public class Professors {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "professor_no")
  private Long professorNo;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "files_no")
  private Files file;

  @Column(name = "professor_name", nullable = false, length = 50)
  private String professorName;

  @Column(name = "position", length = 100)
  private String position;

  @Column(name = "major", length = 100)
  private String major;

  @Column(name = "email", length = 200)
  private String email;

  @Column(name = "tel", length = 100)
  private String tel;

  // 수정 메서드
  public void updateProfessorIfNotNull(
      String professorName, String position, String major, String email, String tel, Files file) {

    if (professorName != null) this.professorName = professorName;
    if (position != null) this.position = position;
    if (major != null) this.major = major;
    if (email != null) this.email = email;
    if (tel != null) this.tel = tel;
    if (file != null) this.file = file;
  }
}

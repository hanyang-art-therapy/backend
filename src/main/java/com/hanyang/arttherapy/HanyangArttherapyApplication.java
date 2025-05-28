package com.hanyang.arttherapy;

import jakarta.persistence.EntityManager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.querydsl.jpa.impl.JPAQueryFactory;

@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
public class HanyangArttherapyApplication {

  public static void main(String[] args) {
    SpringApplication.run(HanyangArttherapyApplication.class, args);
  }

  // JPAQueryFactory 빈 등록
  @Bean
  public JPAQueryFactory jpaQueryFactory(EntityManager em) {
    return new JPAQueryFactory(em);
  }
}

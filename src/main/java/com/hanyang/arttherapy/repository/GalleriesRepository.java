package com.hanyang.arttherapy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hanyang.arttherapy.domain.Galleries;

@Repository
public interface GalleriesRepository extends JpaRepository<Galleries, Long> {}

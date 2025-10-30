package com.sawari.dev.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sawari.dev.model.Analysis;

public interface AnalysisRepository extends JpaRepository<Analysis, Long> {
}

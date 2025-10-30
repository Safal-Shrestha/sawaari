package com.sawari.dev.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sawari.dev.model.Analysis;
import com.sawari.dev.repository.AnalysisRepository;


@RestController
@RequestMapping("/api")
public class GetAnalysisController {
    
    private final AnalysisRepository analysisRepository;

    public GetAnalysisController(AnalysisRepository analysisRepository) {
        this.analysisRepository = analysisRepository;
    }

    // GET
    @GetMapping("/analysisInfo")
    public List<Analysis> getAllAnalysis() {
        return analysisRepository.findAll();
    }
    
}

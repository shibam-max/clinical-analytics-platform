package com.oraclehealth.clinical.controller;

import com.oraclehealth.clinical.service.ClinicalAnalyticsService;
import com.oraclehealth.clinical.service.ClinicalAnalyticsServiceDTOs.*;
import com.oraclehealth.clinical.domain.ClinicalRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/clinical-analytics")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ClinicalAnalyticsController {

    private final ClinicalAnalyticsService clinicalAnalyticsService;

    @PostMapping("/similar-cases")
    public CompletableFuture<ResponseEntity<List<SimilarCaseResult>>> findSimilarCases(
            @RequestBody SimilarCaseRequest request) {
        
        return clinicalAnalyticsService.findSimilarClinicalCases(
                request.getQuery(),
                request.getDemographics(),
                request.getMaxResults(),
                request.getSimilarityThreshold()
        ).thenApply(ResponseEntity::ok);
    }

    @PostMapping("/risk-assessment")
    public CompletableFuture<ResponseEntity<RiskAssessmentResult>> assessRisk(
            @RequestBody ClinicalRiskRequest request) {
        
        return clinicalAnalyticsService.assessClinicalRisk(
                request.getClinicalRecord(),
                request.getPatientHistory()
        ).thenApply(ResponseEntity::ok);
    }

    @PostMapping("/population-health")
    public CompletableFuture<ResponseEntity<PopulationHealthInsights>> analyzePopulation(
            @RequestBody PopulationCriteria criteria) {
        
        return clinicalAnalyticsService.analyzePopulationHealth(criteria)
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping("/decision-support")
    public CompletableFuture<ResponseEntity<ClinicalDecisionSupport>> getDecisionSupport(
            @RequestBody ClinicalContext context) {
        
        return clinicalAnalyticsService.provideClinicalDecisionSupport(context)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Clinical Analytics Service is healthy");
    }

    // Request DTOs
    public static class SimilarCaseRequest {
        private String query;
        private PatientDemographics demographics;
        private int maxResults = 10;
        private double similarityThreshold = 0.8;

        // Getters and setters
        public String getQuery() { return query; }
        public void setQuery(String query) { this.query = query; }
        public PatientDemographics getDemographics() { return demographics; }
        public void setDemographics(PatientDemographics demographics) { this.demographics = demographics; }
        public int getMaxResults() { return maxResults; }
        public void setMaxResults(int maxResults) { this.maxResults = maxResults; }
        public double getSimilarityThreshold() { return similarityThreshold; }
        public void setSimilarityThreshold(double similarityThreshold) { this.similarityThreshold = similarityThreshold; }
    }
}
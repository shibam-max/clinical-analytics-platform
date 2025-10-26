package com.oraclehealth.clinical.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.oraclehealth.clinical.domain.ClinicalRecord;

import java.util.List;
import java.util.UUID;

/**
 * DTOs for Clinical Analytics Service
 */
public class ClinicalAnalyticsServiceDTOs {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PatientDemographics {
        private int age;
        private String gender;
        private double bmi;
        private List<String> comorbidities;
        
        public static PatientDemographics fromRecord(ClinicalRecord record) {
            return PatientDemographics.builder()
                    .age(30) // Default values for demo
                    .gender("unknown")
                    .bmi(25.0)
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimilarCaseResult {
        private String caseId;
        private String patientDemographics;
        private String clinicalPresentation;
        private String diagnosis;
        private String treatment;
        private String outcome;
        private Double similarityScore;
        
        public double getRiskIndicator() {
            return similarityScore != null ? similarityScore : 0.5;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RiskAssessmentResult {
        private UUID patientId;
        private double riskScore;
        private ClinicalAnalyticsService.RiskLevel riskLevel;
        private List<String> contributingFactors;
        private List<String> recommendations;
        private int similarCasesAnalyzed;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PopulationHealthInsights {
        private int insightCount;
        private String summary;
        private java.util.Map<String, Object> insights;
        
        public PopulationHealthInsights() {
            this.insights = new java.util.HashMap<>();
            this.insightCount = 0;
        }
        
        public void addInsight(ClinicalRecord.RecordType recordType, Object analysis) {
            this.insightCount++;
            if (insights == null) insights = new java.util.HashMap<>();
            insights.put(recordType.toString(), analysis);
        }
        
        public void merge(PopulationHealthInsights other) {
            this.insightCount += other.insightCount;
            if (other.insights != null) {
                if (insights == null) insights = new java.util.HashMap<>();
                insights.putAll(other.insights);
            }
        }
        
        public String getSummary() {
            return "Analyzed " + insightCount + " insights";
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClinicalDecisionSupport {
        private List<String> evidenceBasedRecommendations;
        private List<SimilarCaseResult> similarSuccessfulCases;
        private List<String> riskFactors;
        private List<String> contraindications;
        private double confidenceScore;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PopulationCriteria {
        private String condition;
        private String timeRange;
        private String aggregation;
        private int ageMin;
        private int ageMax;
        
        public static PopulationCriteria defaultCriteria() {
            return PopulationCriteria.builder()
                    .condition("general")
                    .timeRange("30d")
                    .aggregation("daily")
                    .ageMin(0)
                    .ageMax(120)
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClinicalContext {
        private UUID patientId;
        private UUID providerId;
        private String clinicalScenario;
        private PatientDemographics patientDemographics;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClinicalRiskRequest {
        private UUID patientId;
        private ClinicalRecord clinicalRecord;
        private List<String> patientHistory;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClinicalFeatures {
        private int age;
        private String condition;
        private List<String> riskFactors;
        
        public double calculateBaseRisk() {
            double risk = 0.3;
            if (age > 65) risk += 0.2;
            if (riskFactors != null && riskFactors.size() > 2) risk += 0.3;
            return Math.min(risk, 1.0);
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnalyticsEvent {
        private String eventType;
        private long timestamp;
        private Object data;
    }
}
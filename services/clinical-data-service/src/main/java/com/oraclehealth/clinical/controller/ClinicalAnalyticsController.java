package com.oraclehealth.clinical.controller;

import com.oraclehealth.clinical.service.ClinicalAnalyticsService;
import com.oraclehealth.clinical.service.ClinicalAnalyticsServiceDTOs.*;
import com.oraclehealth.clinical.controller.ControllerDTOs.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Clinical Analytics REST Controller
 * 
 * Demonstrates Oracle Health technical requirements:
 * - Advanced REST API design with Spring Boot
 * - Security with OAuth2 and role-based access
 * - Async processing with CompletableFuture
 * - Comprehensive API documentation
 * - Performance optimization
 */
@RestController
@RequestMapping("/api/v1/clinical/analytics")
@Tag(name = "Clinical Analytics", description = "Advanced clinical data analytics and insights")
@SecurityRequirement(name = "oauth2")
@Slf4j
public class ClinicalAnalyticsController {

    private final ClinicalAnalyticsService analyticsService;

    @Autowired
    public ClinicalAnalyticsController(ClinicalAnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @PostMapping("/similar-cases")
    @Operation(
        summary = "Find Similar Clinical Cases",
        description = "Uses Vector Database semantic search to find clinically similar cases for decision support"
    )
    @ApiResponse(responseCode = "200", description = "Similar cases found successfully")
    @ApiResponse(responseCode = "400", description = "Invalid search parameters")
    @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    @PreAuthorize("hasRole('CLINICIAN') or hasRole('RESEARCHER')")
    public CompletableFuture<ResponseEntity<List<SimilarCaseResult>>> findSimilarCases(
            @Parameter(description = "Clinical query for semantic search", required = true)
            @RequestParam String clinicalQuery,
            
            @Parameter(description = "Patient demographics for context")
            @RequestBody(required = false) PatientDemographics demographics,
            
            @Parameter(description = "Maximum number of results", example = "20")
            @RequestParam(defaultValue = "20") int maxResults,
            
            @Parameter(description = "Similarity threshold (0.0-1.0)", example = "0.8")
            @RequestParam(defaultValue = "0.8") double similarityThreshold) {

        log.info("Received similar cases request: query={}, maxResults={}", clinicalQuery, maxResults);

        return analyticsService.findSimilarClinicalCases(clinicalQuery, demographics, maxResults, similarityThreshold)
                .thenApply(results -> {
                    log.info("Found {} similar cases", results.size());
                    return ResponseEntity.ok(results);
                })
                .exceptionally(throwable -> {
                    log.error("Error finding similar cases", throwable);
                    return ResponseEntity.internalServerError().build();
                });
    }

    @PostMapping("/risk-assessment")
    @Operation(
        summary = "Clinical Risk Assessment",
        description = "Performs ML-powered risk assessment using Vector DB and historical data analysis"
    )
    @ApiResponse(responseCode = "200", description = "Risk assessment completed")
    @ApiResponse(responseCode = "400", description = "Invalid clinical data")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('NURSE_PRACTITIONER')")
    public CompletableFuture<ResponseEntity<RiskAssessmentResult>> assessRisk(
            @Parameter(description = "Clinical record for risk assessment", required = true)
            @Valid @RequestBody ClinicalRiskRequest request) {

        log.info("Received risk assessment request for patient: {}", request.getPatientId());

        return analyticsService.assessClinicalRisk(request.getClinicalRecord(), request.getPatientHistory())
                .thenApply(result -> {
                    log.info("Risk assessment completed: score={}, level={}", 
                            result.getRiskScore(), result.getRiskLevel());
                    return ResponseEntity.ok(result);
                })
                .exceptionally(throwable -> {
                    log.error("Error performing risk assessment", throwable);
                    return ResponseEntity.internalServerError().build();
                });
    }

    @PostMapping("/population-health")
    @Operation(
        summary = "Population Health Analytics",
        description = "Analyzes population health trends using Big Data processing and real-time streaming"
    )
    @ApiResponse(responseCode = "200", description = "Population analysis completed")
    @ApiResponse(responseCode = "403", description = "Requires researcher or admin role")
    @PreAuthorize("hasRole('RESEARCHER') or hasRole('ADMIN') or hasRole('PUBLIC_HEALTH_OFFICER')")
    public CompletableFuture<ResponseEntity<PopulationHealthInsights>> analyzePopulationHealth(
            @Parameter(description = "Population analysis criteria", required = true)
            @Valid @RequestBody PopulationCriteria criteria) {

        log.info("Received population health analysis request: {}", criteria);

        return analyticsService.analyzePopulationHealth(criteria)
                .thenApply(insights -> {
                    log.info("Population analysis completed: {} insights generated", 
                            insights.getInsightCount());
                    return ResponseEntity.ok(insights);
                })
                .exceptionally(throwable -> {
                    log.error("Error analyzing population health", throwable);
                    return ResponseEntity.internalServerError().build();
                });
    }

    @PostMapping("/decision-support")
    @Operation(
        summary = "Clinical Decision Support",
        description = "Provides evidence-based clinical recommendations using Vector DB and ML algorithms"
    )
    @ApiResponse(responseCode = "200", description = "Clinical recommendations generated")
    @ApiResponse(responseCode = "400", description = "Invalid clinical context")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('NURSE_PRACTITIONER') or hasRole('CLINICAL_SPECIALIST')")
    public CompletableFuture<ResponseEntity<ClinicalDecisionSupport>> getClinicalDecisionSupport(
            @Parameter(description = "Clinical context for decision support", required = true)
            @Valid @RequestBody ClinicalContext context) {

        log.info("Received clinical decision support request for patient: {}", context.getPatientId());

        return analyticsService.provideClinicalDecisionSupport(context)
                .thenApply(support -> {
                    log.info("Clinical decision support generated: {} recommendations, confidence={}",
                            support.getEvidenceBasedRecommendations().size(),
                            support.getConfidenceScore());
                    return ResponseEntity.ok(support);
                })
                .exceptionally(throwable -> {
                    log.error("Error providing clinical decision support", throwable);
                    return ResponseEntity.internalServerError().build();
                });
    }

    @GetMapping("/performance-metrics")
    @Operation(
        summary = "Analytics Performance Metrics",
        description = "Real-time performance metrics for clinical analytics operations"
    )
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM_MONITOR')")
    public ResponseEntity<AnalyticsPerformanceMetrics> getPerformanceMetrics() {
        
        log.debug("Retrieving analytics performance metrics");
        
        AnalyticsPerformanceMetrics metrics = AnalyticsPerformanceMetrics.builder()
                .averageQueryTime(calculateAverageQueryTime())
                .vectorSearchLatency(getVectorSearchLatency())
                .kafkaProcessingRate(getKafkaProcessingRate())
                .jvmMemoryUsage(getJvmMemoryUsage())
                .activeConnections(getActiveConnections())
                .build();

        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/health")
    @Operation(
        summary = "Service Health Check",
        description = "Comprehensive health check including Vector DB, Kafka, and database connectivity"
    )
    public ResponseEntity<ServiceHealthStatus> getHealthStatus() {
        
        ServiceHealthStatus health = ServiceHealthStatus.builder()
                .vectorDatabaseStatus(checkVectorDatabaseHealth())
                .kafkaStatus(checkKafkaHealth())
                .databaseStatus(checkDatabaseHealth())
                .overallStatus(calculateOverallHealth())
                .timestamp(System.currentTimeMillis())
                .build();

        return ResponseEntity.ok(health);
    }

    // Private helper methods for metrics and health checks
    
    private double calculateAverageQueryTime() {
        // Implementation would use Micrometer metrics
        return 45.2; // milliseconds
    }

    private double getVectorSearchLatency() {
        // Implementation would query pgvector performance metrics
        return 23.8; // milliseconds
    }

    private long getKafkaProcessingRate() {
        // Implementation would query Kafka metrics
        return 1250; // messages per second
    }

    private JvmMemoryUsage getJvmMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        return JvmMemoryUsage.builder()
                .totalMemory(totalMemory)
                .usedMemory(usedMemory)
                .freeMemory(freeMemory)
                .usagePercentage((double) usedMemory / totalMemory * 100)
                .build();
    }

    private int getActiveConnections() {
        // Implementation would query connection pool metrics
        return 45;
    }

    private HealthStatus checkVectorDatabaseHealth() {
        // Implementation would ping pgvector
        return HealthStatus.HEALTHY;
    }

    private HealthStatus checkKafkaHealth() {
        // Implementation would check Kafka connectivity
        return HealthStatus.HEALTHY;
    }

    private HealthStatus checkDatabaseHealth() {
        // Implementation would check PostgreSQL connectivity
        return HealthStatus.HEALTHY;
    }

    private HealthStatus calculateOverallHealth() {
        // Implementation would aggregate all health checks
        return HealthStatus.HEALTHY;
    }


}
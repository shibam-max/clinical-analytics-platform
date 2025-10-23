package com.oraclehealth.clinical.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTOs for Clinical Analytics Controller
 */
public class ControllerDTOs {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnalyticsPerformanceMetrics {
        private double averageQueryTime;
        private double vectorSearchLatency;
        private long kafkaProcessingRate;
        private JvmMemoryUsage jvmMemoryUsage;
        private int activeConnections;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JvmMemoryUsage {
        private long totalMemory;
        private long usedMemory;
        private long freeMemory;
        private double usagePercentage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceHealthStatus {
        private HealthStatus vectorDatabaseStatus;
        private HealthStatus kafkaStatus;
        private HealthStatus databaseStatus;
        private HealthStatus overallStatus;
        private long timestamp;
    }

    public enum HealthStatus {
        HEALTHY, DEGRADED, UNHEALTHY
    }
}
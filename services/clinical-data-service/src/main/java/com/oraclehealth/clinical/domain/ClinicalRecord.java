package com.oraclehealth.clinical.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Clinical Record Entity with Vector Database Support
 * 
 * Represents clinical data with semantic search capabilities for Oracle Health platform.
 * Demonstrates advanced JPA usage, vector database integration, and healthcare domain modeling.
 */
@Entity
@Table(name = "clinical_records", indexes = {
    @Index(name = "idx_patient_id", columnList = "patient_id"),
    @Index(name = "idx_provider_id", columnList = "provider_id"),
    @Index(name = "idx_record_type", columnList = "record_type"),
    @Index(name = "idx_encounter_date", columnList = "encounter_date")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClinicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    @Column(name = "provider_id", nullable = false)
    private UUID providerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "record_type", nullable = false)
    private RecordType recordType;

    @Column(name = "title", nullable = false, length = 500)
    private String title;

    @Column(name = "clinical_narrative", columnDefinition = "TEXT")
    private String clinicalNarrative;

    @Column(name = "structured_data", columnDefinition = "JSONB")
    private String structuredData; // JSON for flexible clinical data

    // Vector embedding for semantic search (pgvector)
    @Column(name = "embedding", columnDefinition = "vector(1536)")
    private String embedding;

    @ElementCollection
    @CollectionTable(name = "clinical_record_icd_codes", 
                    joinColumns = @JoinColumn(name = "clinical_record_id"))
    @Column(name = "icd_code")
    private List<String> icdCodes;

    @ElementCollection
    @CollectionTable(name = "clinical_record_cpt_codes", 
                    joinColumns = @JoinColumn(name = "clinical_record_id"))
    @Column(name = "cpt_code")
    private List<String> cptCodes;

    @Column(name = "encounter_date", nullable = false)
    private LocalDateTime encounterDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity_level")
    private SeverityLevel severityLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "confidentiality_level", nullable = false)
    private ConfidentialityLevel confidentialityLevel;

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "facility_id")
    private UUID facilityId;

    // Audit fields for HIPAA compliance
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_by", nullable = false, length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @Version
    @Column(name = "version")
    private Long version;

    // Business methods for clinical operations
    public void updateClinicalNarrative(String narrative, String updatedBy) {
        this.clinicalNarrative = narrative;
        this.updatedBy = updatedBy;
    }

    public void addIcdCode(String icdCode) {
        if (this.icdCodes != null && !this.icdCodes.contains(icdCode)) {
            this.icdCodes.add(icdCode);
        }
    }

    public void addCptCode(String cptCode) {
        if (this.cptCodes != null && !this.cptCodes.contains(cptCode)) {
            this.cptCodes.add(cptCode);
        }
    }

    public boolean isHighRisk() {
        return severityLevel == SeverityLevel.CRITICAL || severityLevel == SeverityLevel.HIGH;
    }

    public boolean requiresSpecialHandling() {
        return confidentialityLevel == ConfidentialityLevel.RESTRICTED || 
               confidentialityLevel == ConfidentialityLevel.CONFIDENTIAL;
    }

    // Enums for clinical data classification
    public enum RecordType {
        DIAGNOSIS,
        TREATMENT_PLAN,
        LAB_RESULT,
        IMAGING_REPORT,
        PROGRESS_NOTE,
        DISCHARGE_SUMMARY,
        MEDICATION_ORDER,
        VITAL_SIGNS,
        PROCEDURE_NOTE,
        CONSULTATION
    }

    public enum SeverityLevel {
        LOW,
        MODERATE,
        HIGH,
        CRITICAL
    }

    public enum ConfidentialityLevel {
        NORMAL,
        RESTRICTED,
        CONFIDENTIAL,
        TOP_SECRET
    }
}
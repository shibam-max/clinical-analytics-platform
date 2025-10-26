package com.oraclehealth.clinical.repository;

import com.oraclehealth.clinical.domain.ClinicalRecord;
import com.oraclehealth.clinical.service.ClinicalAnalyticsServiceDTOs.PopulationCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClinicalDataRepository extends JpaRepository<ClinicalRecord, UUID> {
    
    @Query("SELECT c FROM ClinicalRecord c WHERE c.patientId = :patientId")
    List<ClinicalRecord> findByPatientId(UUID patientId);
    
    @Query("SELECT c FROM ClinicalRecord c WHERE c.recordType = :recordType")
    List<ClinicalRecord> findByRecordType(ClinicalRecord.RecordType recordType);
    
    @Query("SELECT c FROM ClinicalRecord c WHERE c.recordType = 'DIAGNOSIS' AND c.createdAt >= CURRENT_DATE - 30")
    List<ClinicalRecord> findRecentDiagnoses();
    
    default List<ClinicalRecord> findByCriteriaWithOptimization(PopulationCriteria criteria) {
        if ("diabetes".equalsIgnoreCase(criteria.getCondition())) {
            return findRecentDiagnoses();
        }
        return findAll();
    }
}
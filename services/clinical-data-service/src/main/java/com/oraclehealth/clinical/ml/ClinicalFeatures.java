package com.oraclehealth.clinical.ml;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ClinicalFeatures {
    private int age;
    private String gender;
    private double bmi;
    private int heartRate;
    private double bloodPressureSystolic;
    private double bloodPressureDiastolic;
    private double temperature;
    private List<String> symptoms;
    private List<String> riskFactors;
    private List<String> medications;

    public double[] toNumericalVector() {
        return new double[] {
            age / 100.0,
            "male".equalsIgnoreCase(gender) ? 1.0 : 0.0,
            bmi / 50.0,
            heartRate / 200.0,
            bloodPressureSystolic / 200.0,
            bloodPressureDiastolic / 150.0,
            temperature / 110.0,
            symptoms != null ? symptoms.size() / 10.0 : 0.0,
            riskFactors != null ? riskFactors.size() / 10.0 : 0.0,
            medications != null ? medications.size() / 10.0 : 0.0
        };
    }
}
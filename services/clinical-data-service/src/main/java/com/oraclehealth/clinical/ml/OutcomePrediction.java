package com.oraclehealth.clinical.ml;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class OutcomePrediction {
    private String recommendedTreatment;
    private Double successProbability;
    private Map<String, Double> treatmentProbabilities;
    private Double confidenceScore;

    public static OutcomePrediction defaultPrediction() {
        return OutcomePrediction.builder()
                .recommendedTreatment("Standard Care")
                .successProbability(0.5)
                .treatmentProbabilities(new HashMap<>())
                .confidenceScore(0.0)
                .build();
    }
}
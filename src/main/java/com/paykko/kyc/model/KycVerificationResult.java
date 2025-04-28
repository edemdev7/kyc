package com.paykko.kyc.model;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class KycVerificationResult {
    private double facialMatchScore;
    private boolean textualDataMatch;
    private String extractedFirstName;
    private String extractedLastName;
    private String extractedGender;
    private String extractedDateOfBirth;
}
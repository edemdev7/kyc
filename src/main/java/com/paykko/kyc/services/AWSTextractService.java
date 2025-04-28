package com.paykko.kyc.services;

import com.paykko.kyc.model.KycVerificationResult;
import com.paykko.kyc.model.dto.KycSubmissionDTO;

public interface AWSTextractService {
    KycVerificationResult extractAndVerifyData(String photoIdUrl, KycSubmissionDTO submittedData);
}

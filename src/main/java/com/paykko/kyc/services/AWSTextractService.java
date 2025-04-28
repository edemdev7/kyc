package com.paykko.kyc.services;

import com.paykko.kyc.model.KycVerificationResult;
import com.paykko.kyc.model.dto.KycSubmissionDTO;

/**
 * Interface that defines a service for interacting with AWS Textract to extract and verify
 * information from a provided identification document image. The extracted information
 * is compared against the submitted KYC data to validate identity.
 *
 * Functionalities:
 * - Extracts text and relevant data fields from an image of an identity document using AWS Textract.
 * - Compares the extracted data with the user-submitted KYC data to verify identity details.
 *
 * Parameters:
 * - photoIdUrl: URL of the identification document image, typically stored in Amazon S3.
 * - submittedData: DTO containing user-declared identity information for comparison.
 *
 * Returns:
 * - An instance of KycVerificationResult containing the results of the extraction and
 *   verification process, including whether the textual data matches and the extracted
 *   identity information.
 */
public interface AWSTextractService {
    KycVerificationResult extractAndVerifyData(String photoIdUrl, KycSubmissionDTO submittedData);
}

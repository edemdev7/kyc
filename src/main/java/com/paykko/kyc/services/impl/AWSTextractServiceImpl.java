package com.paykko.kyc.services.impl;

import com.paykko.kyc.common.exception.KycVerificationException;
import com.paykko.kyc.model.KycVerificationResult;
import com.paykko.kyc.model.dto.KycSubmissionDTO;
import com.paykko.kyc.services.AWSTextractService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Service implementation for AWS Textract to handle document text extraction
 * and KYC (Know Your Customer) data verification.
 * This class interacts with AWS Textract to analyze document images provided
 * in Amazon S3 and compares extracted data with user-submitted KYC information.
 */
@Service
@RequiredArgsConstructor
public class AWSTextractServiceImpl implements AWSTextractService {

    private final TextractClient textractClient;

    @Override
    public KycVerificationResult extractAndVerifyData(String photoIdUrl, KycSubmissionDTO submittedData) {
        try {
            DetectDocumentTextRequest request = DetectDocumentTextRequest.builder()
                    .document(Document.builder()
                            .s3Object(S3Object.builder()
                                    .name(getS3KeyFromUrl(photoIdUrl))
                                    .bucket(getS3BucketFromUrl(photoIdUrl))
                                    .build())
                            .build())
                    .build();

            DetectDocumentTextResponse response = textractClient.detectDocumentText(request);
            Map<String, String> extractedData = extractRelevantData(response.blocks());

            return KycVerificationResult.builder()
                    .extractedFirstName(extractedData.get("firstName"))
                    .extractedLastName(extractedData.get("lastName"))
                    .extractedGender(extractedData.get("gender"))
                    .extractedDateOfBirth(extractedData.get("dateOfBirth"))
                    .textualDataMatch(verifyExtractedData(extractedData, submittedData))
                    .build();

        } catch (Exception e) {
            throw new KycVerificationException("Erreur lors de l'extraction du texte: " + e.getMessage());
        }
    }

    private Map<String, String> extractRelevantData(List<Block> blocks) {
        String fullText = blocks.stream()
                .filter(block -> block.blockType() == BlockType.LINE)
                .map(Block::text)
                .collect(Collectors.joining(" "));

        return parseIdCardText(fullText);
    }

    private Map<String, String> parseIdCardText(String fullText) {
        Map<String, String> extractedData = new HashMap<>();

        Pattern namePattern = Pattern.compile("Nom: ([^\\n]+)");
        Pattern firstNamePattern = Pattern.compile("Prénom: ([^\\n]+)");

        Matcher nameMatcher = namePattern.matcher(fullText);
        if (nameMatcher.find()) {
            extractedData.put("lastName", nameMatcher.group(1).trim());
        }

        // Ajoutez d'autres extractions selon vos besoins...

        return extractedData;
    }

    private boolean verifyExtractedData(Map<String, String> extractedData, KycSubmissionDTO submittedData) {
        boolean firstNameMatch = compareStrings(extractedData.get("firstName"), submittedData.getFirstName());
        boolean lastNameMatch = compareStrings(extractedData.get("lastName"), submittedData.getLastName());
        boolean genderMatch = compareStrings(extractedData.get("gender"), submittedData.getGender());
        boolean dobMatch = compareStrings(extractedData.get("dateOfBirth"), submittedData.getDateOfBirth());

        return firstNameMatch && lastNameMatch && genderMatch && dobMatch;
    }

    private boolean compareStrings(String s1, String s2) {
        if (s1 == null || s2 == null) {
            return false;
        }
        return s1.trim().equalsIgnoreCase(s2.trim());
    }

    private String getS3KeyFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    private String getS3BucketFromUrl(String url) {
        String[] parts = url.split("/");
        return parts[2].split("\\.")[0];
    }
}
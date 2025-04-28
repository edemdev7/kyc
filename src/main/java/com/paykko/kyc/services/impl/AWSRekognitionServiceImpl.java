package com.paykko.kyc.services.impl;

import com.paykko.kyc.common.exception.KycVerificationException;
import com.paykko.kyc.services.AWSRekognitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;

import java.util.List;

/**
 * Implementation of AWSRekognitionService to interact with AWS Rekognition for comparing faces.
 * This service uses the AWS Rekognition SDK to compare two images stored in Amazon S3 and
 * calculates the similarity score between the faces present in those images.
 *
 * The compareFaces method takes two image URLs as inputs, extracts the S3 bucket and key
 * information from the URLs, and sends a CompareFacesRequest to the AWS Rekognition service.
 * It processes the response to determine the highest similarity score among detected matches.
 *
 * Dependencies:
 * - RekognitionClient: The AWS SDK client used for communicating with AWS Rekognition.
 *
 * Exceptions:
 * - Throws KycVerificationException in case of an error during the process.
 */
@Service
@RequiredArgsConstructor
public class AWSRekognitionServiceImpl implements AWSRekognitionService {

    private final RekognitionClient rekognitionClient;

    @Override
    public double compareFaces(String photoIdUrl, String selfieUrl) {
        try {
            CompareFacesRequest request = CompareFacesRequest.builder()
                    .sourceImage(Image.builder()
                            .s3Object(S3Object.builder()
                                    .name(getS3KeyFromUrl(photoIdUrl))
                                    .bucket(getS3BucketFromUrl(photoIdUrl))
                                    .build())
                            .build())
                    .targetImage(Image.builder()
                            .s3Object(S3Object.builder()
                                    .name(getS3KeyFromUrl(selfieUrl))
                                    .bucket(getS3BucketFromUrl(selfieUrl))
                                    .build())
                            .build())
                    .similarityThreshold(80f)
                    .build();

            CompareFacesResponse response = rekognitionClient.compareFaces(request);
            List<CompareFacesMatch> faceMatches = response.faceMatches();

            if (faceMatches.isEmpty()) {
                return 0.0;
            }

            return faceMatches.stream()
                    .mapToDouble(match -> match.similarity().doubleValue())
                    .max()
                    .orElse(0.0);

        } catch (Exception e) {
            throw new KycVerificationException("Erreur lors de la comparaison des visages: " + e.getMessage());
        }
    }

    private String getS3KeyFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    private String getS3BucketFromUrl(String url) {
        String[] parts = url.split("/");
        return parts[2].split("\\.")[0];
    }
}
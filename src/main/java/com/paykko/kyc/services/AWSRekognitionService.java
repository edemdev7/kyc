package com.paykko.kyc.services;

/**
 * This interface defines a service for interacting with AWS Rekognition to compare faces.
 * It provides functionality to compute the similarity score between two images.
 */
public interface AWSRekognitionService {
    double compareFaces(String photoIdUrl, String selfieUrl);
}

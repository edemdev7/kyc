package com.paykko.kyc.common.exception;

public class KycNotFoundException extends RuntimeException {
    public KycNotFoundException(String message) {
        super(message);
    }
}


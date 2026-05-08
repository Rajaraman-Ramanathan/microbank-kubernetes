package com.microbank.auth.dto.event;

public record ActivationEvent(
        String email,
        String firstName,
        String lastName,
        String activationCode
) {
}
package com.microbank.notification.event;

public record ActivationEvent(
        String email,
        String firstName,
        String lastName,
        String activationCode
) {
}
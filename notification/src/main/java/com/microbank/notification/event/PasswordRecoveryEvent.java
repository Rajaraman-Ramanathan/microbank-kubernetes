package com.microbank.notification.event;

public record PasswordRecoveryEvent(
        String email,
        String passwordRecoveryCode
) {
}
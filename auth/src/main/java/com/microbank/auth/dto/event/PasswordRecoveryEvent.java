package com.microbank.auth.dto.event;

public record PasswordRecoveryEvent(
        String email,
        String passwordRecoveryCode
) {
}
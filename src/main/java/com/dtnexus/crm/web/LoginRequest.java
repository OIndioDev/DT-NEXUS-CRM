package com.dtnexus.crm.web;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank String tenant,
        @NotBlank String username,
        @NotBlank String password) {
}

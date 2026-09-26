package com.dtnexus.crm.web;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ServiceCatalogRequest(
        @NotBlank(message = "tenant é obrigatório")
        @Size(max = 80, message = "tenant deve ter no máximo 80 caracteres")
        String tenant,
        @NotBlank(message = "serviceName é obrigatório")
        @Size(max = 120, message = "serviceName deve ter no máximo 120 caracteres")
        String serviceName,
        @Size(max = 80, message = "category deve ter no máximo 80 caracteres")
        String category,
        @NotNull(message = "costPrice é obrigatório")
        @DecimalMin(value = "0.00", inclusive = true, message = "costPrice não pode ser negativo")
        BigDecimal costPrice,
        @NotNull(message = "salePrice é obrigatório")
        @DecimalMin(value = "0.00", inclusive = true, message = "salePrice não pode ser negativo")
        BigDecimal salePrice,
        Boolean active) {
}

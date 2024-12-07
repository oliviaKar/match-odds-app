package com.okarath.assessment.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateOddDto (
    @NotNull Double odd
){}

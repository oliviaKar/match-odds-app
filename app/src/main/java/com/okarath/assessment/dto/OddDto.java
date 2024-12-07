package com.okarath.assessment.dto;

import com.okarath.assessment.entity.Specifier;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record OddDto(
        Long id,
        @NotNull Specifier specifier,
        @NotNull Double odd
){

}
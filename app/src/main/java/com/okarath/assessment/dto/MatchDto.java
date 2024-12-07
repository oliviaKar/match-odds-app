package com.okarath.assessment.dto;

import com.okarath.assessment.entity.Sport;
import com.okarath.assessment.validation.TeamsNotEqual;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

import java.util.HashSet;
import java.util.Set;

@Builder
@TeamsNotEqual
public record MatchDto(
        Long id,
        @Pattern(regexp = "^\\d{2}/\\d{2}/\\d{4}$", message = "Date must be in the format dd/MM/yyyy") String date,
        @Pattern(regexp = "^\\d{2}:\\d{2}$", message = "Date must be in the format HH:mm") String time,
        String description,
        @NotNull String teamA,
        @NotNull String teamB,
        @NotNull Sport sport,
        Set<OddDto> odds
) {
}

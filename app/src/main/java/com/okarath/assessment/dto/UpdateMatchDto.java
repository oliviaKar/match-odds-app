package com.okarath.assessment.dto;

import jakarta.validation.constraints.Pattern;

public record UpdateMatchDto(
        @Pattern(regexp = "^\\d{2}:\\d{2}$", message = "Time must be in the format HH:mm") String time,
        String description
) {
}

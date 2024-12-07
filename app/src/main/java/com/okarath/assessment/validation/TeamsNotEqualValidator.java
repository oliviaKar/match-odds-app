package com.okarath.assessment.validation;

import com.okarath.assessment.dto.MatchDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class TeamsNotEqualValidator implements ConstraintValidator<TeamsNotEqual, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value instanceof MatchDto v) {
            return v.teamA()!= null && v.teamB() != null
                    && !Objects.equals(v.teamA().toLowerCase(), v.teamB().toLowerCase());
        }
        return false;
    }
}
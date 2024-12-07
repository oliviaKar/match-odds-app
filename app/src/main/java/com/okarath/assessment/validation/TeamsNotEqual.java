package com.okarath.assessment.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = TeamsNotEqualValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TeamsNotEqual {

    String message() default "TeamA & TeamB must not be equal";
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
package com.flow_assignment.file_extension.extension.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ExtensionNameValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExtensionName {
    String message() default "규칙에 맞는 확장자를 입력해주세요.(처음/마지막 글자 . 불가, 공백 불가, 대문자 불가, 글자 수)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
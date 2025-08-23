package com.flow_assignment.file_extension.extension.validation;

import com.flow_assignment.file_extension.extension.ExtensionProperties;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ExtensionNameValidator implements ConstraintValidator<ExtensionName, String> {

    private final ExtensionProperties extensionProperties;

    @Override
    public boolean isValid(String name, ConstraintValidatorContext context) {
        if (name.length() > extensionProperties.maxLength() || !ExtensionNameRules.isValid(name)) {
            return false;
        }
        return true;
    }
}

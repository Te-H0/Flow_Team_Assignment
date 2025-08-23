package com.flow_assignment.file_extension.extension.dto;

import com.flow_assignment.file_extension.extension.ExtensionProjection;

import java.util.List;

public record ManageExtensionResponse(
        List<ExtensionProjection> fixedExtensions,
        List<ExtensionProjection> customExtensions,
        int maxCustomExtensionCount,
        int maxExtensionLength,
        String extensionRegex
) {
}

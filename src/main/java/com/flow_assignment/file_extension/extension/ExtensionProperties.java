package com.flow_assignment.file_extension.extension;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="extension")
public record ExtensionProperties(int maxCustomCount, int maxLength) {
}

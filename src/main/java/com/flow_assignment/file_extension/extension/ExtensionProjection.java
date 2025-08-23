package com.flow_assignment.file_extension.extension;

public interface ExtensionProjection {
    Long getId();

    String getName();

    ExtensionType getType();

    boolean getActive();
}

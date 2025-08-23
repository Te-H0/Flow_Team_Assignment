package com.flow_assignment.file_extension.support;

import com.flow_assignment.file_extension.extension.ExtensionProjection;
import com.flow_assignment.file_extension.extension.ExtensionType;

public class TestExtensionProjection implements ExtensionProjection {
    Long id;
    String name;
    ExtensionType type;
    boolean active;

    public TestExtensionProjection(Long id, String name, ExtensionType type, boolean active) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.active = active;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ExtensionType getType() {
        return type;
    }

    @Override
    public boolean getActive() {
        return active;
    }
}
package com.flow_assignment.file_extension.extension;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExtensionRepository extends JpaRepository<Extension, Long> {

    List<ExtensionProjection> findExtensionByTypeOrderById(ExtensionType extensionType);

    List<ExtensionProjection> findExtensionByTypeOrderByIdDesc(ExtensionType extensionType);

    Optional<Extension> findExtensionByName(String name);

    int countByTypeAndActive(ExtensionType extensionType, boolean active);
}

package com.flow_assignment.file_extension.extension;

import com.flow_assignment.file_extension.extension.validation.ExtensionNameRules;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "extension",
        indexes = {
                @Index(name = "idx_extension_type_active", columnList = "type, is_active")
        }
)
public class Extension {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ExtensionType type;

    @Column(name = "is_active")
    private boolean active;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime modifiedAt;

    // Custom 전용 생성자
    public Extension(String name) {
        ExtensionNameRules.isValid(name);
        this.name = name;
        this.type = ExtensionType.CUSTOM;
        this.active = true;
    }

    // 추후, Fixed 확장자 추가를 해야할 경우 등,,,
    public Extension(String name, ExtensionType type, boolean isActive) {
        ExtensionNameRules.isValid(name);
        this.name = name;
        this.type = type;
        this.active = isActive;
    }

    // 요청한 active 상태로 변경
    public void toggleActive(boolean active) {
        this.active = active;
    }

}

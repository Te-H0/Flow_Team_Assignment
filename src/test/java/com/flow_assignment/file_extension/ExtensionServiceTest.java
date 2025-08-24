package com.flow_assignment.file_extension;

import com.flow_assignment.file_extension.extension.*;
import com.flow_assignment.file_extension.extension.dto.CreateCustomExtensionResponse;
import com.flow_assignment.file_extension.extension.dto.ManageExtensionResponse;
import com.flow_assignment.file_extension.extension.exception.*;
import com.flow_assignment.file_extension.support.TestExtensionProjection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExtensionServiceTest {

    private final int MAX_EXTENSION_LENGTH = 5;
    private final int MAX_CUSTOM_EXTENSION_COUNT = 10;

    private final Long existingNoActiveFixedExtensionId = 1L;
    private final Long existingActiveFixedExtensionId = 2L;
    private final Long noExistingFixedExtensionId = 100L;

    private final Long existingCustomExtensionId = 8L;
    private final Long noExistingCustomExtensionId = 101L;

    @Mock
    private ExtensionRepository extensionRepository;
    @Mock
    private ExtensionProperties extensionProperties;
    @InjectMocks
    private ExtensionService extensionService;

    private List<ExtensionProjection> createFixedExtensionProjections() {
        return List.of(new TestExtensionProjection(1L, "bat", ExtensionType.FIXED, false),
                new TestExtensionProjection(2L, "cmd", ExtensionType.FIXED, false),
                new TestExtensionProjection(3L, "com", ExtensionType.FIXED, false),
                new TestExtensionProjection(4L, "cpl", ExtensionType.FIXED, false),
                new TestExtensionProjection(5L, "exe", ExtensionType.FIXED, false),
                new TestExtensionProjection(6L, "scr", ExtensionType.FIXED, false),
                new TestExtensionProjection(7L, "js", ExtensionType.FIXED, false));
    }

    private List<ExtensionProjection> createCustomExtensionProjections() {
        return List.of(new TestExtensionProjection(8L, "pdf", ExtensionType.CUSTOM, true),
                new TestExtensionProjection(9L, "dll", ExtensionType.CUSTOM, true),
                new TestExtensionProjection(10L, "msi", ExtensionType.CUSTOM, true),
                new TestExtensionProjection(11L, "ksh", ExtensionType.CUSTOM, true),
                new TestExtensionProjection(12L, "php", ExtensionType.CUSTOM, true),
                new TestExtensionProjection(13L, "vbs", ExtensionType.CUSTOM, true),
                new TestExtensionProjection(14L, "ps1", ExtensionType.CUSTOM, true));
    }

    private Extension createFixedExtension(Long id, String name, boolean active) {
        Extension extension = new Extension(name, ExtensionType.FIXED, active);
        // 리플렉션 활용한 id 강제 주입
        ReflectionTestUtils.setField(extension, "id", id);
        return extension;
    }

    private Extension createCustomExtension(Long id, String name) {
        Extension extension = new Extension(name);
        // 리플렉션 활용한 id 강제 주입
        ReflectionTestUtils.setField(extension, "id", id);
        return extension;
    }


    @Test
    @DisplayName("확장자 관리 페이지 진입 시, 올바른 데이터 조회 확인")
    void getManageExtensionTest() {
        // Given
        List<ExtensionProjection> fixedExtensionProjections = createFixedExtensionProjections();
        List<ExtensionProjection> customExtensionProjections = createCustomExtensionProjections();

        when(extensionRepository.findExtensionByTypeOrderById(ExtensionType.FIXED))
                .thenReturn(fixedExtensionProjections);
        when(extensionRepository.findExtensionByTypeOrderByIdDesc(ExtensionType.CUSTOM))
                .thenReturn(customExtensionProjections);

        when(extensionProperties.maxCustomCount()).thenReturn(MAX_CUSTOM_EXTENSION_COUNT);
        when(extensionProperties.maxLength()).thenReturn(MAX_EXTENSION_LENGTH);

        // When
        ManageExtensionResponse manageExtensionResponse = extensionService.getManageExtension();

        // Then
        assertThat(manageExtensionResponse.fixedExtensions()).
                allMatch(extension -> extension.getType() == ExtensionType.FIXED);

        assertThat(manageExtensionResponse.customExtensions()).
                allMatch(extension -> extension.getType() == ExtensionType.CUSTOM);

        assertThat(manageExtensionResponse.maxCustomExtensionCount()).
                isEqualTo(MAX_CUSTOM_EXTENSION_COUNT);

        assertThat(manageExtensionResponse.maxExtensionLength()).
                isEqualTo(MAX_EXTENSION_LENGTH);

    }

    @Test
    @DisplayName("고정 확장자 Toggle 정상 동작")
    void updateFixedExtensionActiveTest() {
        // Given
        Extension activeFixedExtension = createFixedExtension(existingActiveFixedExtensionId, "bat", true);
        Extension noActiveFixedExtension = createFixedExtension(existingNoActiveFixedExtensionId, "cmd", false);

        when(extensionRepository.findById(existingActiveFixedExtensionId))
                .thenReturn(Optional.of(activeFixedExtension));
        when(extensionRepository.findById(existingNoActiveFixedExtensionId))
                .thenReturn(Optional.of(noActiveFixedExtension));

        // When
        extensionService.updateFixedExtensionActive(existingNoActiveFixedExtensionId, true);
        extensionService.updateFixedExtensionActive(existingActiveFixedExtensionId, false);


        // Then
        assertThat(noActiveFixedExtension.isActive()).isTrue();
        assertThat(activeFixedExtension.isActive()).isFalse();
    }

    @Test
    @DisplayName("고정 확장자 Toggle 요청 시, 존재하는 고정 확장자가 없을 경우 NoExistingFixedExtensionIdException")
    void updateFixedExtensionActiveThrowExtensionNotFoundExceptionTest() {
        // Given
        when(extensionRepository.findById(noExistingFixedExtensionId)).thenReturn(Optional.empty());

        // When and Then
        assertThrows(ExtensionNotFoundException.class, () -> extensionService.updateFixedExtensionActive(noExistingFixedExtensionId, true));
    }

    @Test
    @DisplayName("고정 확장자 Toggle 요청 시, 사용자가 보는 상태와 서버의 상태가 다를 경우 ExtensionStateConflictException")
    void updateFixedExtensionActiveThrowExtensionStateConflictExceptionTest() {
        // Given
        Extension noActiveFixedExtension = createFixedExtension(existingNoActiveFixedExtensionId, "cmd", false);

        when(extensionRepository.findById(existingNoActiveFixedExtensionId))
                .thenReturn(Optional.of(noActiveFixedExtension));

        // When and Then
        assertThrows(ExtensionStateConflictException.class, ()
                -> extensionService.updateFixedExtensionActive(existingNoActiveFixedExtensionId, false));
    }

    @Test
    @DisplayName("새로운 Custom 확장자 생성")
    void createCustomExtensionTest() {
        // Given
        String newCustomExtensionName = "pdf";

        when(extensionProperties.maxCustomCount())
                .thenReturn(MAX_CUSTOM_EXTENSION_COUNT);

        when(extensionRepository.countByTypeAndActive(ExtensionType.CUSTOM, true))
                .thenReturn(0);
        when(extensionRepository.findExtensionByName(newCustomExtensionName)).thenReturn(Optional.empty());
        when(extensionRepository.save(Mockito.<Extension>any()))
                .thenAnswer(invocation -> {
                    Extension arg = invocation.getArgument(0); // extensionRepository.save()에 들어가는 Parameter
                    ReflectionTestUtils.setField(arg, "id", noExistingCustomExtensionId);
                    return arg;
                });

        // When
        CreateCustomExtensionResponse response =
                extensionService.createCustomExtension(newCustomExtensionName);

        // Then
        assertThat(response.id()).isEqualTo(noExistingCustomExtensionId);
        assertThat(response.name()).isEqualTo(newCustomExtensionName);
    }

    @Test
    @DisplayName("새로운 Custom 확장자 생성 시, 생성 가능한 확장자 수를 초과한 경우 CustomExtensionLimitExceededException")
    void createCustomExtensionThrowCustomExtensionLimitExceededExceptionTest() {
        // Given
        String newCustomExtensionName = "pdf";

        when(extensionRepository.countByTypeAndActive(ExtensionType.CUSTOM, true))
                .thenReturn(MAX_CUSTOM_EXTENSION_COUNT);
        when(extensionProperties.maxCustomCount())
                .thenReturn(MAX_CUSTOM_EXTENSION_COUNT);

        // When and Then
        assertThrows(CustomExtensionLimitExceededException.class, () -> extensionService.createCustomExtension(newCustomExtensionName));
    }

    @Test
    @DisplayName("새로운 Custom 확장자 생성 시, 이름 형식 올바르지 않을 경우 ExtensionNameBadRequestException")
    void createCustomExtensionThrowExtensionNameBadRequestExceptionTest() {
        // Given
        String newCustomExtensionWrongName = ". pdf";
        when(extensionRepository.countByTypeAndActive(ExtensionType.CUSTOM, true))
                .thenReturn(0);
        when(extensionProperties.maxCustomCount())
                .thenReturn(MAX_CUSTOM_EXTENSION_COUNT);

        // When and Then
        assertThrows(ExtensionNameBadRequestException.class, () -> extensionService.createCustomExtension(newCustomExtensionWrongName));
    }

    @Test
    @DisplayName("새로운 Custom 확장자 생성 시, 같은 이름의 Fixed 확장자가 존재하는 경우 ConflictWithFixedExtensionException")
    void createCustomExtensionThrowConflictWithFixedExtensionExceptionTest() {
        // Given
        String existFixedExtensionName = "bat";
        Extension activeFixedExtension = createFixedExtension(existingActiveFixedExtensionId, existFixedExtensionName, true);

        when(extensionRepository.countByTypeAndActive(ExtensionType.CUSTOM, true))
                .thenReturn(0);
        when(extensionProperties.maxCustomCount())
                .thenReturn(MAX_CUSTOM_EXTENSION_COUNT);
        when(extensionRepository.findExtensionByName(existFixedExtensionName)).thenReturn(Optional.of(activeFixedExtension));

        // When and Then
        assertThrows(ConflictWithFixedExtensionException.class, () -> extensionService.createCustomExtension(existFixedExtensionName));
    }

    @Test
    @DisplayName("새로운 Custom 확장자 생성 시, 같은 이름의 Custom 확장자가 존재하는 경우 DuplicateCustomExtensionNameException")
    void createCustomExtensionThrowDuplicateExtensionNameExceptionTest() {
        // Given
        String existCustomExtensionName = "pdf";
        Extension customExtension = createCustomExtension(existingCustomExtensionId, existCustomExtensionName);

        when(extensionRepository.countByTypeAndActive(ExtensionType.CUSTOM, true))
                .thenReturn(0);
        when(extensionProperties.maxCustomCount())
                .thenReturn(MAX_CUSTOM_EXTENSION_COUNT);
        when(extensionRepository.findExtensionByName(existCustomExtensionName)).thenReturn(Optional.of(customExtension));

        // When and Then
        assertThrows(DuplicateCustomExtensionNameException.class, () -> extensionService.createCustomExtension(existCustomExtensionName));
    }

    @Test
    @DisplayName("존재하는 Custom 확장자 삭제")
    void deleteCustomExtensionTest() {
        // Given
        String existCustomExtensionName = "pdf";
        Extension customExtension = createCustomExtension(existingCustomExtensionId, existCustomExtensionName);

        when(extensionRepository.findById(existingCustomExtensionId)).thenReturn(Optional.of(customExtension));

        // When
        extensionService.deleteCustomExtension(existingCustomExtensionId);

        // Then
        verify(extensionRepository, times(1)).delete(customExtension);
    }

    @Test
    @DisplayName("존재하는 Custom 확장자 삭제 시, 존재하지 않는 확장자일 경우 ExtensionNotFoundException")
    void deleteCustomExtensionThrowExtensionNotFoundExceptionTest() {
        // Given
        when(extensionRepository.findById(noExistingCustomExtensionId)).thenReturn(Optional.empty());

        // When and Then
        assertThrows(ExtensionNotFoundException.class, () -> extensionService.deleteCustomExtension(noExistingCustomExtensionId));
    }


}

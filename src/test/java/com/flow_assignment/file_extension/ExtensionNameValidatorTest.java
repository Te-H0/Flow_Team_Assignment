package com.flow_assignment.file_extension;

import com.flow_assignment.file_extension.extension.ExtensionProperties;
import com.flow_assignment.file_extension.extension.validation.ExtensionNameValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExtensionNameValidatorTest {

    private final int MAX_NAME_LENGTH = 5;
    @Mock
    private ExtensionProperties extensionProperties;
    @InjectMocks
    private ExtensionNameValidator extensionNameValidator;

    @BeforeEach
    void setUp() {
        when(extensionProperties.maxLength()).thenReturn(MAX_NAME_LENGTH);
    }

    @Test
    @DisplayName("알파벳 소문자로 구성된 올바른 이름")
    void validNameTest1() {
        // Given
        String name = "teho";

        // When
        boolean result = extensionNameValidator.isValid(name, null);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("알파벳 소문자와 중간에 . 으로 구성된 올바른 이름")
    void validNameTest2() {
        // Given
        String name = "te.ho";

        // When
        boolean result = extensionNameValidator.isValid(name, null);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("알바펫 소문자와 중간에 숫자가 포함된 올바른 이름")
    void validTextTest3() {
        // Given
        String name = "a123b";

        // When
        boolean result = extensionNameValidator.isValid(name, null);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("빈 문자열인 잘못된 이름")
    void emptyNameTest() {
        // Given
        String name = "";

        // When
        boolean result = extensionNameValidator.isValid(name, null);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("공백인 잘못된 이름")
    void blankNameTest() {
        // Given
        String name = " ";

        // When
        boolean result = extensionNameValidator.isValid(name, null);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("중간에 공백이 포함된 잘못된 이름")
    void nameWithBlankInsideTest() {
        // Given
        String name = "pd f";

        // When
        boolean result = extensionNameValidator.isValid(name, null);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("최대 이름 길이를 초과한 잘못된 이름")
    void longNameTest() {
        // Given
        String name = "longnameeeee";

        // When
        boolean result = extensionNameValidator.isValid(name, null);

        // Then
        assertThat(result).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"AA", "aA", "Abc", "qw.C"})
    @DisplayName("대문자가 포함된 잘못된 이름")
    void upperCaseNameTest(String name) {

        // When
        boolean result = extensionNameValidator.isValid(name, null);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("접두사가 .인 잘못된 이름")
    void inValidPrefixTest() {
        // Given
        String name = ".exe";

        // When
        boolean result = extensionNameValidator.isValid(name, null);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("접미사가 .인 잘못된 이름")
    void inValidSuffixTest() {
        // Given
        String name = "exe.";

        // When
        boolean result = extensionNameValidator.isValid(name, null);

        // Then
        assertThat(result).isFalse();
    }
}

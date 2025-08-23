package com.flow_assignment.file_extension.extension.validation;

import lombok.AllArgsConstructor;

import java.util.regex.Pattern;

@AllArgsConstructor
public class ExtensionNameRules {
    // 첫 글자 문자 시작, .다음에는 무조건 영어 소문자 / 숫자, 마지막 글자 . 이 될 수 없음
    public static final Pattern PATTERN =
            Pattern.compile("^[a-z0-9]+(\\.[a-z0-9]+)*$");

    // null 불가, "" 불가, 공백포함 절대 금지
    public static boolean isValid(String name) {
        return name != null
                && !name.isBlank()
                && !name.contains(" ")
                && PATTERN.matcher(name).matches();
    }
}

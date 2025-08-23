package com.flow_assignment.file_extension.extension;


import com.flow_assignment.file_extension.extension.dto.CreateCustomExtensionResponse;
import com.flow_assignment.file_extension.extension.dto.ManageExtensionResponse;
import com.flow_assignment.file_extension.extension.validation.ExtensionName;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@Controller
@AllArgsConstructor
@Slf4j
@RequestMapping("/extensions/manage")
public class ExtensionController {
    private final ExtensionService extensionService;

    @GetMapping
    public String manageExtension(Model model) {
        ManageExtensionResponse manageExtensionData = extensionService.getManageExtension();

        // Fixed/Custom 확장자 정보
        // 최대 추가 가능 확장자 수, 확장자 이름 최대 길이, 정규식 서버와 통일 시켜 사용하기.
        model.addAttribute("maxCustomExtensionCount", manageExtensionData.maxCustomExtensionCount());
        model.addAttribute("maxCustomExtensionLength", manageExtensionData.maxExtensionLength());
        model.addAttribute("fixedExtensions", manageExtensionData.fixedExtensions());
        model.addAttribute("customExtensions", manageExtensionData.customExtensions());
        model.addAttribute("extensionRegex", manageExtensionData.extensionRegex());

        return "extensions-manage";
    }

    @PatchMapping("/fixed/{id}")
    @ResponseBody
    public ResponseEntity<String> toggleFixedExtension(@PathVariable Long id, @RequestParam boolean active) {
        extensionService.updateFixedExtensionActive(id, active);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/custom")
    @ResponseBody
    public ResponseEntity<CreateCustomExtensionResponse> createCustomExtension(@ExtensionName @RequestParam String name) {
        return ResponseEntity.status(HttpStatus.CREATED).body(extensionService.createCustomExtension(name));
    }

    @DeleteMapping("/custom/{id}")
    public ResponseEntity<String> deleteCustomExtension(@PathVariable Long id) {
        extensionService.deleteCustomExtension(id);
        return ResponseEntity.noContent().build();
    }
}

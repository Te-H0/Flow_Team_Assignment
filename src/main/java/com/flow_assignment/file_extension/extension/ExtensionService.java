package com.flow_assignment.file_extension.extension;

import com.flow_assignment.file_extension.extension.dto.CreateCustomExtensionResponse;
import com.flow_assignment.file_extension.extension.dto.ManageExtensionResponse;
import com.flow_assignment.file_extension.extension.exception.*;
import com.flow_assignment.file_extension.extension.validation.ExtensionNameRules;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class ExtensionService {

    private final ExtensionRepository extensionRepository;
    private final ExtensionProperties extensionProperties;

    /**
     * manage extension 화면에 필요한 정보
     *
     * @return Fixed/Custom 확장자 정보, 최대 추가 가능 확장자 수, 확장자 이름 최대 길이
     */
    @Transactional(readOnly = true)
    public ManageExtensionResponse getManageExtension() {
        return new ManageExtensionResponse(
                getExtensionsByType(ExtensionType.FIXED),
                getExtensionsByType(ExtensionType.CUSTOM),
                getMaxCustomExtensionCount(),
                getMaxExtensionLength(),
                ExtensionNameRules.PATTERN.pattern()
        );
    }

    /**
     * Fixed 확장자 toggle (존재하는 확장자인지, 사용자가 보고 있는 active 상태와 DB에서 상태 일치 여부 검증)
     *
     * @param id     확장자 id
     * @param active 원하는 active 상태
     */
    @Transactional
    public void updateFixedExtensionActive(long id, boolean active) {
        Extension extension = getExtensionById(id);
        validateExtensionActiveState(active, extension);
        extension.toggleActive(active);
    }

    /**
     * Custom 확장자 생성 (Custom 확장자 총 개수 유효성 검증, 이미 존재하는 Fixed/Custom 이름 여부 검증)
     *
     * @param name 생성할 Custom 확장자 이름
     * @return 생성된 Custom 확장자 id, name
     */
    @Transactional
    public CreateCustomExtensionResponse createCustomExtension(String name) {
        validateCustomExtensionCountLimit(1);
        validateCustomExtensionCreatable(name);

        Extension saved = createAndSaveCustomExtensionByName(name);

        return new CreateCustomExtensionResponse(saved.getId(), saved.getName());
    }

    /**
     * 확장자 삭제 (존재하는 확장자인지 검증)
     *
     * @param id 삭제할 확장자 id
     */
    @Transactional
    public void deleteCustomExtension(long id) {
        extensionRepository.delete(getExtensionById(id));
    }

    /**
     * @return Custom 확장자 최대 개수
     */
    private int getMaxCustomExtensionCount() {
        return extensionProperties.maxCustomCount();
    }

    /**
     * @return 확장자 이름 최대 길이
     */
    private int getMaxExtensionLength() {
        return extensionProperties.maxLength();
    }

    /**
     * Fixed 확장자일 경우 -> id 오름차순
     * Custom 확장자일 경우 -> id 내림차순
     * (화면 기획 의도와 맞춤)
     *
     * @param extensionType FIXED, CUSTOM
     * @return 일치하는 확장자들의 ExtensionProjection(id, name, type, active)
     */
    private List<ExtensionProjection> getExtensionsByType(ExtensionType extensionType) {
        return extensionType == ExtensionType.FIXED ? extensionRepository.findExtensionByTypeOrderById(extensionType)
                : extensionRepository.findExtensionByTypeOrderByIdDesc(extensionType);
    }

    /**
     * ex. 사용자가 check를 요청했는데, 이미 DB에서는 check인 경우 Exception
     *
     * @param active    변경 원하는 상태
     * @param extension 변경할 확장자
     */
    private void validateExtensionActiveState(boolean active, Extension extension) {
        log.info("기존 상태 {}, 변경 후 상태 {}", extension.isActive(), active);
        if (extension.isActive() == active) {
            throw new ExtensionStateConflictException();
        }
    }

    /**
     * name으로 확장자 생성
     *
     * @param name 생성할 확장자 이름
     * @return 생성된 확장자 entity
     */
    private Extension createAndSaveCustomExtensionByName(String name) {
        Extension extension = new Extension(name);
        return extensionRepository.save(extension);
    }

    /**
     * DB에 저장된 custom 확장자 + 생성할 확장자 수 > 최대 확장자 수 인지 검증
     * 추후에 기능 확장이 진행됐을 때, 현재 DB 상태 검증 등 다양하게 재사용하기 위해 파라미터 적용
     *
     * @param newExtension 추가할 확장자 수
     */
    private void validateCustomExtensionCountLimit(int newExtension) {
        if (extensionRepository.countByTypeAndActive(ExtensionType.CUSTOM, true) + newExtension > getMaxCustomExtensionCount()) {
            throw new CustomExtensionLimitExceededException();
        }
    }

    /**
     * 생성할 확장자의 이름이 이미 존재하는지 검증
     *
     * @param name 생성할 확장자 이름
     */
    private void validateCustomExtensionCreatable(String name) {
        if (!ExtensionNameRules.isValid(name)) {
            throw new ExtensionNameBadRequestException(); // 이름이 형식에 맞지 않을 경우
        }
        extensionRepository.findExtensionByName(name).ifPresent(extension -> {
            if (extension.getType() == ExtensionType.FIXED) { // 이미 같은 이름의 Fixed 확장자가 있는 경우
                throw new ConflictWithFixedExtensionException();
            } else if (extension.getType() == ExtensionType.CUSTOM) { // 이미 같은 이름의 Custom 확장자가 있는 경우
                throw new DuplicateExtensionNameException();
            }
        });

    }

    /**
     * @param id 찾을 확장자 id
     * @return 찾은 확장자 entity
     */
    private Extension getExtensionById(long id) {
        return extensionRepository.findById(id).orElseThrow(ExtensionNotFoundException::new);
    }
}

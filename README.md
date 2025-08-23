# 파일 확장자 차단 과제
작성자 : 이태호<br>
이메일 : teho.lee200@gmail.com

> 화면 접속 주소 (스프링 실행 후) 

> UI : http://localhost:8080/extensions/manage

> DB : http://localhost:8080/h2-console/<br>
 → JDBC URL : jdbc:h2:mem:file_extension<br>
 → User Name : sa<br>
 → Password :

<br>

# 확장자 이름 규칙 정의
- 접두사, 접미사에 . 을 허용하지 않았습니다.
- 접두사, 접미사를 제외하고 . 을 허용했습니다. Ex) tar.gz, min.js
- 숫자를 허용했습니다. Ex) mp3, 3gp
- 공백은 절대 불가입니다. Ex) “ pdf”, “p df”, “pdf “
    - trim을 사용하여 제거하는 방법도 고민하였지만,
    사용자의 입력이 공백 이외에도 있을 수 있다 판단하였습니다.
- 사용 가능한 문자는 소문자 알파벳, 숫자, . 으로 정의하였습니다.
- 대문자를 허용하지 않았습니다.
- 적용한 정규식 → `^[a-z0-9]+(\.[a-z0-9]+)*$`

<br>

# 고려했던 점

## Fixed, Custom Entity 분리하지 않은 이유

- 두 개의 확장자 Entity를 별도로 분리하지 않고, 같은 Exstension Entity 내에서
컬럼의 ExtensionType enum으로 구분하였습니다.
- 추후에 Fixed 확장자가 Custom 확장자로 또는 Custom 확장자가 Fixed 확장자 로 변경되는 상황을 고려했습니다. 하나의 컬럼만 변경하는 것이, 기존 table에서 삭제하고 새로운 table에 저장하는 것보다 편리하다 판단했습니다.
- 기능이 확장되고 프로젝트 규모가 크다면, Extension을 baseEntity로 사용하여 Fixed/Custom을 분리하는 선택을 했을 것 입니다.

<br>

## ExtensionType enum

- Extension Entity에서 Fixed, Custom의 상태를 Enum으로 표현하였습니다.
- 현재는 두 가지이기 때문에 boolean도 가능하지만, 추천/기존에 존재했던,, 등 기능 확장 가능성이 있는 데이터라 판단했기 때문입니다.\
  
<br>

## Index

- type은 단독으로 자주 조회에 사용되었고, active는 type과 함께 조회에 사용되었기 때문에 Index의 성질을 활용하여 (type, active) 복합 인덱스를 생성하였습니다.
- name은 unique 설정으로 자동으로 Index가 생성되기에 작성하지 않았습니다.
- JPA를 활용하여 Entity 내부에서 생성하였습니다.
  
<br>

## 확장자 이름 최대 입력 길이, 커스텀 확장자 수 저장 위치

- 해당 제한 조건을 Service에서 상수로 갖을 수도 있지만, 외부 설정(application.yml)에 작성하여 ExtensionProperties를 통해 접근하였습니다.

![yml](https://github.com/user-attachments/assets/0ab26e28-9071-4f80-aca7-749373539d54)
![properties](https://github.com/user-attachments/assets/0ba8eb53-5c37-41ca-9ebf-57de123b1f22)

- 선택의 이유는 확장자들의 속성을 별도로 관리할 수 있고, yml에 작성했을 경우 서버 재시작 없이 설정을 변경할 수도 있고,
- yml에 정의했으므로 프로필에 맞게 (text, prod, dev,,) 값을 유연하게 설정할 수 있다는 장점이 있어 해당 방법을 선택했습니다.
- record와 조합하여 불변의 값으로 사용할 수 있게 했습니다.
- 확장자 이름 최대 입력 길이, 커스텀 확장자 수는 서비스 기획의도에 따라 변경될 수 있지만,<br>정규식의 경우 변동 가능성이 낮은 규격이기 때문에 상수로 사용하였습니다.

<br>

## 제약 사항 공유

- 최대 입력 길이, 최대 커스텀 확장자 수, 정규식을 View와 공유하며 유지 보수 성과 제약 사항 일치 안전성을 확보하였습니다.

<br>

## 사용자 입력 검증

- 정의했던 확장자 이름 규칙에 적합한지 확인하는 것은 이후에 기능이 다양해질 경우 빈번히 일어날 수 있다 판단하여,<br>파라미터에서 입력값 검증을 위해 @ExtensionName 커스텀 어노테이션을 구현했습니다.

<br>

## Custom Exception

- 서비스에 맞는 Exception으로 재정의 하였습니다.
- Exception 이름, 상태 코드, Error Message를 재정의하여 사용자/프론트 개발자가 있을 경우 의사소통이 원활하게 가능하도록 했습니다.

<br>

## 이미 존재하는 이름의 확장자를 추가하는 경우

- Fixed / Custom에 이미 존재하는 경우, 두가지로 분기 처리 하였습니다.
- 상황에 맞는 Exception을 처리하여 어떤 상황인지 확인할 수 있게 했습니다.

<br>

## 예외 케이스 2단 검증

- 글자 수 초과, Custom 확장자 수 초과, Custom 확장자 입력 형태 오류, 예상치 못한 오류 (DB에 데이터 삭제로 인한 값 불일치)
- 위의 모든 상황을 클라이언트(thymeleaf), 서버에서 모두 검증을 진행하여 예외 상황을 안정적으로 대처했습니다.

<br>




## 단위 테스트

- 서비스 레이어의 모든 분기 점을 단위 테스트를 통해 예상한 값이 나오는지 검증했습니다.
- @ExtensionName 커스텀 어노테이션에서 입력 값의 유효성을 검증하는 메서드를 통해 예상 가능한 입력 값들에 대한 테스트를 진행하여 제약의 유효성을 검증했습니다.

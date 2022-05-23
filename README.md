## 1. 회원가입 API
![회원가입api설명](https://user-images.githubusercontent.com/52727315/169742650-f3acf2e0-ea4b-40b9-823f-674b1e16528a.png)

--------------------------------------------------------------------------------------------------------------------------------------------

### 1-1. 전체 동작 확인  
***1-1-1. 요청***
- URL: localhost:8080/szs/signup
- 파라미터
```json
{
  "name": "홍길동",
  "password": 123456,
  "regNo": "860824-1655068",
  "userId": "hong12"
}
```
![회원가입req](https://user-images.githubusercontent.com/52727315/169742779-5df95634-0db3-402a-8043-d6396968c700.png)

***1-1-2. 응답***

![회원가입res](https://user-images.githubusercontent.com/52727315/169742962-90cd3cc1-ac4f-4a0c-872f-09f336de12d7.png)

--------------------------------------------------------------------------------------------------------------------------------------------

### 1-2. 요구사항 구현

***1-2-1. 필수 파라미터 체크***

- 아래와 같이 Bean Validation를 사용하여 Dto에서 유효성 검사를 하도록 구현하였습니다
```java
...
@NotEmpty(message = "Please enter your userId")
@Size(min = 4, message = "userId should have at least 4 characters")
@ApiModelProperty(name = "userId", value = "사용자 ID", example = "hong12", required = true)
private String userId;

@NotEmpty(message = "Please enter your password")
@Size(min = 4, message = "password should have at least 4 characters")
@ApiModelProperty(name = "password", value = "사용자 PW", example = "123456", required = true)
private String password;

@NotEmpty(message = "Please enter your name")
@Size(min = 2, message = "name should have at least 2 characters")
@ApiModelProperty(name = "name", value = "사용자 이름", example = "홍길동", required = true)
private String name;

@NotEmpty(message = "Please enter your regNo(ID card number)")
@Size(min = 14, message = "regNo(ID card number) should have at least 14 characters")
@ApiModelProperty(name = "regNo", value = "사용자 주민번호", example = "860824-1655068", required = true)
private String regNo;
...
```
- 이와 같이 userId 필드를 제거하고, password의 값을 빈 공백으로 해서 요청을 보내면 아래와 같은 응답값을 내려줍니다
```json
{
  "name": "홍길동",
  "password": "",
  "regNo": "860824-1655068"
}
```
![회원가입파라미터체크응답](https://user-images.githubusercontent.com/52727315/169744258-3f87bc27-1407-489b-8ab3-1a51f41a60ef.png)  
<br>  
***1-2-2 회원가입 유저 제한***

- 지정된 회원인지 체크하는 함수를 만들어 체크하는 로직을 추가하였습니다
```java
public void limitSingup(JoinReqDto requestDto) {
  Map<String, String> group = new HashMap<>();
  group.put("홍길동", "860824-1655068");
  group.put("김둘리", "921108-1582816");
  group.put("마징가", "880601-2455116");
  group.put("베지터", "910411-1656116");
  group.put("손오공", "820326-2715702");

  String regNo = group.get(requestDto.getName());

  if (!StringUtils.hasText(regNo) || !regNo.equals(requestDto.getRegNo()))
      throw new IllegalArgumentException("지정회원 외엔 가입이 불가능합니다");
}
```

- 지정된 5명 이외의 요청이 들어올 경우 아래와 같은 응답값을 내려줍니다
```json
{
  "name": "송문준",
  "password": "123456",
  "regNo": "860824-1655068",
  "userId": "hong12"
}
```
![회원가입지정회원](https://user-images.githubusercontent.com/52727315/169746122-f941b298-8873-4494-83b0-aea7b331db85.png)

--------------------------------------------------------------------------------------------------------------------------------------------
--------------------------------------------------------------------------------------------------------------------------------------------

## 2. 로그인 API
![로그인설명](https://user-images.githubusercontent.com/52727315/169746967-2cd21064-ecf5-4031-8174-3e7eebe79a31.png)

--------------------------------------------------------------------------------------------------------------------------------------------

### 2-1. 전체 동작 확인  
***2-1-1. 요청***
- URL: localhost:8080/szs/login
- 파라미터
```json
{
  "password": "123456",
  "userId": "hong12"
}
```
![로그인req](https://user-images.githubusercontent.com/52727315/169747138-29470e63-ed02-4966-a0f7-52b8880d3f69.png)



***2-1-2. 응답***

![로그인res](https://user-images.githubusercontent.com/52727315/169747224-9e872fd1-ef23-4641-a260-020da834d296.png)

--------------------------------------------------------------------------------------------------------------------------------------------

### 2-2. 요구사항 구현

***2-2-1. 필수 파라미터 체크***


******

******



--------------------------------------------------------------------------------------------------------------------------------------------

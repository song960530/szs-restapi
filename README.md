# 삼쩜삼 백엔드 엔지니어 채용 과제
- 이름: 송문준
- Swagger 주소: localhost:8080/swagger-ui.html  
(주관식 관련 답변은 제일 하단에 적혀있습니다)

--------------------------------------------------------------------------------------------------------------------------------------------
--------------------------------------------------------------------------------------------------------------------------------------------

## 1. 회원가입
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

## 2. 로그인
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

- 아래와 같이 Bean Validation를 사용하여 Dto에서 유효성 검사를 하도록 구현하였습니다
```java
@NotEmpty(message = "Please enter your userId")
@Size(min = 1, message = "userId should have at least 1 characters")
@ApiModelProperty(name = "userId", value = "사용자 ID", example = "hong12", required = true)
private String userId;

@NotEmpty(message = "Please enter your password")
@Size(min = 1, message = "password should have at least 1 characters")
@ApiModelProperty(name = "password", value = "사용자 PW", example = "123456", required = true)
private String password;
```

- 이와 같이 password의 값을 빈 공백으로 해서 요청을 보내면 아래와 같은 응답값을 내려줍니다
```json
{
  "password": "",
  "userId": "hong12"
}
```

![로그인검증res](https://user-images.githubusercontent.com/52727315/169749682-141aeed1-5fee-459f-8f70-12c5ed01808e.png)  
<br>  
***2-2-2. token 생성***

- 페이로드에 userId, roles, 발행시간, 만료시간을 넣은 토큰을 생성합니다
```java
    // JWT 토큰 생성
    public String createToken(String userPk, List<Role> roles) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("alg", "HS256");

        Date now = new Date();
        Claims claims = Jwts.claims().setSubject(userPk); // JWT payload에 저장되는 정보단위
        List<EnumRole> roleList = roles.stream().map(Role::getRoles).collect(Collectors.toList());
        claims.put("roles", roleList);

        return Jwts.builder()
                .setHeader(headers) // 헤더 설정
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발생 시간 정보
                .setExpiration(new Date(now.getTime() + jwtProperties.getTokenValidTime())) // 만료시간
                .signWith(SignatureAlgorithm.HS256, encSecretKey) // 암호화 및 encSecretKey 세팅
                .compact();
    }
```  
--------------------------------------------------------------------------------------------------------------------------------------------
--------------------------------------------------------------------------------------------------------------------------------------------

## 3. 내 정보 보기

![내정보조회설명](https://user-images.githubusercontent.com/52727315/169763966-b92b6e72-b4bf-4f20-a8a1-d9c361ec5b64.png)

--------------------------------------------------------------------------------------------------------------------------------------------

### 3-1. 전체 동작 확인  
***3-1-1. 요청***
- URL: localhost:8080/szs/me
- 파라미터
```
로그인시 발급받은 type과 토큰값을 header Authorization 값에 "[type] [토큰값]"으로 넣어서 요청한다
```

![내정보req](https://user-images.githubusercontent.com/52727315/169765903-22299168-2c10-4887-b7d4-2698638bdbd0.png)

***3-1-2. 응답***

![내정보res](https://user-images.githubusercontent.com/52727315/169766047-be2111dc-0dd8-436a-a14c-520146fe89e8.png)

--------------------------------------------------------------------------------------------------------------------------------------------

### 3-2. 요구사항 구현

***3-2-1. 인증 토큰 이용하여 자기 정보 조회***

- 스프링 시큐리티의 설정파일에 addFilterBefore을 통해 UsernamePasswordAuthenticationFilter보다 커스텀 JWT토큰 필터가 우선 실행되도록 설정
```java
...
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 토큰 기반 인증이라 세션을 사용하지 않음
                .and()
                .authorizeRequests()
                .antMatchers("/szs/me", "/szs/scrap", "/szs/refund").hasRole("USER")
                .anyRequest().permitAll()
                .and()
                // UsernamePasswordAuthenticationFilter 동작 전 Jwt 필터 설정
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
        ;
    }
...
```

- 커스텀 JWT토큰 필터에서 헤더값에 있는 토큰을 조회한 후 토큰의 유효성검증을 마쳐서 SecurityContext에 인증정보를 저장
```java
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) request); // 헤더에서 JWT 받기

        // 토큰의 유효성 검증
        if (StringUtils.hasText(token) && jwtTokenProvider.validToken(token)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(token);  // 토큰이 유효하면 유저 정보를 받아온다
            SecurityContextHolder.getContext().setAuthentication(authentication);   // SecurityContext에 Authentication 객체 저장
        }

        chain.doFilter(request, response);
    }
```

- 커스텀 @LoginCheck 어노테이션이 붙은 함수의 경우 로그언체크 및 요청IP와 로그인시의 IP 비교(토큰 탈취 당했을시를 방지)
```java
    @Before("@annotation(com.codetest.szsrestapi.global.annotation.LoginCheck)")
    public void loginCheck() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        if (authentication == null || AnonymousAuthenticationToken.class.isAssignableFrom(authentication.getClass())) {
            throw new IllegalStateException("로그인 정보가 없습니다");
        }

        String clientIp = userService.findClientIp(request);
        UserIp userIp = userService.findUserLoginIp(authentication.getName());

        // 로그인시 저장된 IP와 요청 IP가 다를경우 동작
        if (userIp == null || !userIp.getLoginIp().equals(clientIp))
            throw new IllegalStateException("재로그인 해주세요");
    }
```

- 위 절차를 정상적으로 거친 이후 응답 처리

--------------------------------------------------------------------------------------------------------------------------------------------
--------------------------------------------------------------------------------------------------------------------------------------------  

## 4. 유저정보 스크랩

![스크랩설명](https://user-images.githubusercontent.com/52727315/169770164-a7dfd362-9b18-4cee-9f62-0a4c2fede54d.png)

--------------------------------------------------------------------------------------------------------------------------------------------

### 4-1. 전체 동작 확인  
***4-1-1. 요청***
- URL: localhost:8080/szs/scrap
- 파라미터
```
로그인시 발급받은 type과 토큰값을 header Authorization 값에 "[type] [토큰값]"으로 넣어서 요청한다
```

![스크랩req](https://user-images.githubusercontent.com/52727315/169770362-7e6ae2d9-beed-44af-a7dc-3cab4ceda3d3.png)

***4-1-2. 응답***

```json
{
  "status": 200,
  "message": "동기스크랩",
  "data": {
    "jsonList": {
      "scrap002": [
        {
          "총사용금액": "2,000,000",
          "소득구분": "산출세액"
        }
      ],
      "scrap001": [
        {
          "소득내역": "급여",
          "총지급액": "24,000,000",
          "업무시작일": "2020.10.03",
          "기업명": "(주)활빈당",
          "이름": "홍길동",
          "지급일": "2020.11.02",
          "업무종료일": "2020.11.02",
          "주민등록번호": "860824-1655068",
          "소득구분": "근로소득(연간)",
          "사업자등록번호": "012-34-56789"
        }
      ],
      "errMsg": "",
      "company": "삼쩜삼",
      "svcCd": "test01",
      "userId": "1"
    },
    "appVer": "2021112501",
    "hostNm": "jobis-codetest",
    "workerResDt": "2022-05-23T07:51:06.959565",
    "workerReqDt": "2022-05-23T07:51:06.959628"
  }
}
```

--------------------------------------------------------------------------------------------------------------------------------------------

### 4-2. 요구사항 구현

***4-2-1. 인증 토큰 이용하여 자기 정보 조회***
- 위 3-2-1 내용과 중복되므로 생략하겠습니다  
<br>  

***4-2-2. 스크랩URL을 통한 데이터 중 계산에 필요한 값 저장***
- restTemplate를 사용하여 스크랩URL 호출 -> 응답받은 json데이터를 파싱 -> scrap001의 총지급액과 scrap002의 총사용금액을 DB에 저장

--------------------------------------------------------------------------------------------------------------------------------------------
--------------------------------------------------------------------------------------------------------------------------------------------  

## 5. 유저의 환급액 조회

![환급액설명](https://user-images.githubusercontent.com/52727315/169774307-c1554e00-07e7-4f53-93d8-f1445ade55a5.png)

--------------------------------------------------------------------------------------------------------------------------------------------

### 5-1. 전체 동작 확인  
***5-1-1. 요청***
- URL: localhost:8080/szs/refund
- 파라미터
```
로그인시 발급받은 type과 토큰값을 header Authorization 값에 "[type] [토큰값]"으로 넣어서 요청한다
```

![환급액req](https://user-images.githubusercontent.com/52727315/169774854-f7aa8c6d-83ef-4002-960e-0fb04baf44ba.png)

***5-1-2. 응답***

![환급액res](https://user-images.githubusercontent.com/52727315/169774970-0437045f-fae8-4a5b-990c-fe64430997ac.png)

--------------------------------------------------------------------------------------------------------------------------------------------

### 5-2. 요구사항 구현

***5-2-1. 인증 토큰 이용하여 자기 정보 조회***
- 위 3-2-1 내용과 중복되므로 생략하겠습니다  
<br>  

***5-2-2. 계산식으로 환급액 조회***
- DB에 저장되어있던 "총지급액, 총사용금액"을 통하여 "한도,공제액,환급액" 각각의 계산식을 호출하여 계산
```
...
double salary = scrap.getSalary(); // 총지급액
double useAmount = scrap.getUseAmount(); // 총사용금액
int limit = calcLimit(salary); // 한도
int deductedAmount = calcDeductedAmount(useAmount); // 공제액
int refundAmount = calcRefundAmount(limit, deductedAmount); // 환급액
...
```
--------------------------------------------------------------------------------------------------------------------------------------------
--------------------------------------------------------------------------------------------------------------------------------------------  

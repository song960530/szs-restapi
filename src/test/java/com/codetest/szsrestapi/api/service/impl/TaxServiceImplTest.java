package com.codetest.szsrestapi.api.service.impl;

import com.codetest.szsrestapi.api.entity.ScrapHistory;
import com.codetest.szsrestapi.api.entity.User;
import com.codetest.szsrestapi.api.exception.ScrapApiException;
import com.codetest.szsrestapi.api.repository.ScrapHistoryRepository;
import com.codetest.szsrestapi.api.repository.ScrapRepository;
import com.codetest.szsrestapi.api.repository.UserRepository;
import com.codetest.szsrestapi.global.config.properties.AES256Properties;
import com.codetest.szsrestapi.global.config.properties.SzsScrapProperties;
import com.codetest.szsrestapi.global.util.cipher.AES256Util;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest
@ActiveProfiles("dev")
@ExtendWith(MockitoExtension.class)
class TaxServiceImplTest {
    private TaxServiceImpl taxService;
    private AES256Util aes256Util;
    private SzsScrapProperties scrapProperties;
    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;

    @Mock
    private UserRepository userRepository;
    @Mock
    private ScrapRepository scrapRepository;
    @Mock
    private ScrapHistoryRepository scrapHistoryRepository;

    @BeforeEach
    void setUp() {
        aes256Util = new AES256Util(new AES256Properties("szsrestapisecretszsrestapisecret", "szsrestapisecret"));
        aes256Util.init();
        scrapProperties = new SzsScrapProperties("https://codetest.3o3.co.kr/v1/scrap");
        restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        taxService = new TaxServiceImpl(aes256Util, restTemplate, scrapProperties, userRepository, scrapRepository, scrapHistoryRepository);
    }

    @Test
    @DisplayName("SecurityContext에서 Authentication 가져오기")
    @WithMockUser(username = "test1", password = "123456", roles = {"USER"})
    public void getAuthenticationFromSecurityContext() throws Exception {
        // given
        User userMock = mock(User.class);

        // when
        doReturn(Optional.of(userMock)).when(userRepository).findByUserId(anyString());

        // then
        User user = taxService.findUserIdFromAuth();
        assertNotNull(user);
        assertEquals(userMock, user);
    }

    @Test
    @DisplayName("SecurityContext에서 Authentication ID로 조회안됐을때")
    @WithMockUser(username = "test1", password = "123456", roles = {"USER"})
    public void notFoundUser() throws Exception {
        // given
        User userMock = mock(User.class);

        // when
        doReturn(Optional.empty()).when(userRepository).findByUserId(anyString());

        // then
        assertThrows(IllegalArgumentException.class, () -> {
            taxService.findUserIdFromAuth();
        });
    }

    @Test
    @DisplayName("apiCallTest")
    public void apiCallTest() throws Exception {
        // given
        User userMock = mock(User.class);
        when(userMock.getName()).thenReturn("손오공");
        when(userMock.getRegNo()).thenReturn(aes256Util.encrypt("820326-2715702"));
        String body = makeResBody();

        // when
        mockServer.expect(requestTo("https://codetest.3o3.co.kr/v1/scrap"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(body, MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = taxService.callScrapApi(userMock);

        // then
        mockServer.verify();
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONObject object = new JSONObject(response).getJSONObject("body").getJSONObject("data").getJSONObject("jsonList");
        assertEquals("손오공", object.getJSONArray("scrap001").getJSONObject(0).getString("이름"));
        assertEquals("820326-2715702", object.getJSONArray("scrap001").getJSONObject(0).getString("주민등록번호"));

    }

    @Test
    @DisplayName("스크랩 시도이력 히스토리 저장")
    public void recordHistory() throws Exception {
        // given
        User userMock = mock(User.class);
        ScrapHistory scrapMock = mock(ScrapHistory.class);
        ResponseEntity resMock = mock(ResponseEntity.class);
        when(resMock.getStatusCode()).thenReturn(HttpStatus.OK);
        when(resMock.getBody()).thenReturn("");

        // when
        doReturn(scrapMock).when(scrapHistoryRepository).save(any(ScrapHistory.class));

        // then
        ScrapHistory scrapHistory = taxService.recordScrapHistory(userMock, resMock);
        assertEquals(scrapMock.getClass(), scrapHistory.getClass());
    }

    @Test
    @DisplayName("스크랩완료 후 데이터 저장")
    public void recordAfterScrap() throws Exception {
        // given
        User user = mock(User.class);
        ScrapHistory scrapHistory = mock(ScrapHistory.class);
        JSONObject jsonObject = new JSONObject(makeResBody());

        // when

        // then
        taxService.recordScrap(user, jsonObject, scrapHistory);
    }

    @Test
    @DisplayName("스크랩데이터 파싱도중 에러발생")
    public void errorWhenParsingScrapData() throws Exception {
        // given
        User user = mock(User.class);
        ScrapHistory scrapHistory = mock(ScrapHistory.class);

        // when

        // then
        assertThrows(ScrapApiException.class, () -> {
            taxService.recordScrap(user, new JSONObject(), scrapHistory);
        });
    }

    @Test
    @DisplayName("스크랩 정상처리")
    @WithMockUser(username = "test1", password = "123456", roles = {"USER"})
    public void finishScrap() throws Exception {
        // given
        User user = mock(User.class);
        when(user.getName()).thenReturn("손오공");
        when(user.getRegNo()).thenReturn(aes256Util.encrypt("820326-2715702"));
        ScrapHistory scrapHistory = mock(ScrapHistory.class);

        // when
        doReturn(Optional.of(user)).when(userRepository).findByUserId(anyString());
        mockServer.expect(requestTo("https://codetest.3o3.co.kr/v1/scrap"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(makeResBody(), MediaType.APPLICATION_JSON));
        doReturn(scrapHistory).when(scrapHistoryRepository).save(any(ScrapHistory.class));

        // then
        taxService.scrap();
    }

    @Test
    @DisplayName("스크랩api status값이 fail 일때")
    @WithMockUser(username = "test1", password = "123456", roles = {"USER"})
    public void scrapStatusFail() throws Exception {
        // given
        User user = mock(User.class);
        when(user.getName()).thenReturn("손오공");
        when(user.getRegNo()).thenReturn(aes256Util.encrypt("820326-2715702"));

        // when
        doReturn(Optional.of(user)).when(userRepository).findByUserId(anyString());
        mockServer.expect(requestTo("https://codetest.3o3.co.kr/v1/scrap"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"status\": \"fail\", \"errors\": {\"code\": \"-1\", \"message\": \"요청하신 값은 스크랩 가능유저가 아닙니다\" }}", MediaType.APPLICATION_JSON));

        // then
        assertThrows(ScrapApiException.class, () -> {
            taxService.scrap();
        });
    }

    @Test
    @DisplayName("스크랩 시도없이 환불금 조회하였을경우 예외처리")
    @WithMockUser(username = "test1", password = "123456", roles = {"USER"})
    public void refundBeforeNoScrap() throws Exception {
        // given
        User user = mock(User.class);

        // when
        doReturn(Optional.of(user)).when(userRepository).findByUserId(anyString());
        doReturn(Optional.empty()).when(scrapHistoryRepository).findTopByUser(any(User.class));

        // then
        assertThrows(IllegalStateException.class, () -> {
            taxService.refund();
        });
    }

    @Test
    @DisplayName("스크랩을 시도는하였지만 정상처리가 되지 않았을 때 예외처리")
    @WithMockUser(username = "test1", password = "123456", roles = {"USER"})
    public void abnormalProcessing() throws Exception {
        // given
        User user = mock(User.class);
        ScrapHistory scrapHistory = mock(ScrapHistory.class);

        // when
        doReturn(Optional.of(user)).when(userRepository).findByUserId(anyString());
        doReturn(Optional.of(scrapHistory)).when(scrapHistoryRepository).findTopByUser(any(User.class));
        doReturn(Optional.empty()).when(scrapRepository).findByUserAndScrapHistory(any(User.class), any(ScrapHistory.class));

        // then
        assertThrows(IllegalStateException.class, () -> {
            taxService.refund();
        });
    }

    @Test
    @DisplayName("두숫자중 작은 숫자 구하기")
    public void min() throws Exception {
        // given

        // when

        // then
        assertEquals(10_000, taxService.calcRefundAmount(10_000, 20_000));
    }

    @Test
    @DisplayName("총사용금액이 잘못 들어왔을 때")
    public void useAmountUnderZero() throws Exception {
        // given

        // when

        // then
        assertThrows(IllegalArgumentException.class, () -> {
            taxService.calcDeductedAmount(-1);
        });
    }

    @Test
    @DisplayName("사용금액이 130만원 이하일 경우")
    public void useAmountUnder130() throws Exception {
        // given

        // when

        // then
        assertEquals(550_000, taxService.calcDeductedAmount(1_000_000));
    }

    @Test
    @DisplayName("사용금액이 130만원을 초과하였을 경우")
    public void useAmountUpper130() throws Exception {
        // given

        // when

        // then
        assertEquals(925_000, taxService.calcDeductedAmount(2_000_000));
    }

    @Test
    @DisplayName("한도 계산중 총급여액이 잘못들어옴")
    public void failCaclLimit() throws Exception {
        // given

        // when

        // then
        assertThrows(IllegalArgumentException.class, () -> {
            taxService.calcLimit(-1);
        });
    }

    @Test
    @DisplayName("총급여액이 3300만 이하일때 한도계산")
    public void calcUnder3300() throws Exception {
        // given

        // when

        // then
        assertEquals(740_000, taxService.calcLimit(10_000_000));
    }

    @Test
    @DisplayName("총급여액이 3300~7000만 사이일때 계산금액이 66만보다 작을경우")
    public void calcBetween3300And7000_1() throws Exception {
        // given

        // when

        // then
        assertEquals(660_000, taxService.calcLimit(50_000_000));
    }

    @Test
    @DisplayName("총급여액이 3300~7000만 사이일때 계산금액이 66만보다 클경우")
    public void calcBetween3300And7000_2() throws Exception {
        // given

        // when

        // then
        assertEquals(684_000, taxService.calcLimit(40_000_000));
    }

    @Test
    @DisplayName("총급여액이 7000만 초과일때 계산금액이 50만보다 작을경우")
    public void calcUpper7000_1() throws Exception {
        // given

        // when

        // then
        assertEquals(500_000, taxService.calcLimit(100_000_000));
    }

    @Test
    @DisplayName("총급여액이 7000만 초과일때 계산금액이 50만보다 클경우")
    public void calcUpper7000_2() throws Exception {
        // given

        // when

        // then
        assertEquals(610_000, taxService.calcLimit(70_100_000));
    }

    @Test
    @DisplayName("금액한글로변환로직_1")
    public void convertMoneyString_1() throws Exception {
        // given

        // when

        // then
        assertEquals("60만원", taxService.convertMoneyString(600_000));
    }

    @Test
    @DisplayName("금액한글로변환로직_2")
    public void convertMoneyString_2() throws Exception {
        // given

        // when

        // then
        assertEquals("60만 4천원", taxService.convertMoneyString(604_000));
    }

    @Test
    @DisplayName("금액한글로변환로직_3")
    public void convertMoneyString_3() throws Exception {
        // given

        // when

        // then
        assertEquals("60만 4백원", taxService.convertMoneyString(600_400));
    }

    private String makeResBody() {
        return "{\n" +
                "  \"status\": \"success\",\n" +
                "  \"data\": {\n" +
                "    \"jsonList\": {\n" +
                "      \"scrap002\": [\n" +
                "        {\n" +
                "          \"총사용금액\": \"1,333,333.333\",\n" +
                "          \"소득구분\": \"산출세액\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"scrap001\": [\n" +
                "        {\n" +
                "          \"소득내역\": \"급여\",\n" +
                "          \"총지급액\": \"94,666.666\",\n" +
                "          \"업무시작일\": \"2020.10.03\",\n" +
                "          \"기업명\": \"지구행성0-1\",\n" +
                "          \"이름\": \"손오공\",\n" +
                "          \"지급일\": \"2020.11.02\",\n" +
                "          \"업무종료일\": \"2020.11.02\",\n" +
                "          \"주민등록번호\": \"820326-2715702\",\n" +
                "          \"소득구분\": \"근로소득(연간)\",\n" +
                "          \"사업자등록번호\": \"012-23-12345\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"errMsg\": \"\",\n" +
                "      \"company\": \"삼쩜삼\",\n" +
                "      \"svcCd\": \"test01\",\n" +
                "      \"userId\": 5\n" +
                "    },\n" +
                "    \"appVer\": \"2021112501\",\n" +
                "    \"hostNm\": \"jobis-codetest\",\n" +
                "    \"workerResDt\": \"2022-05-22T06:54:42.685441\",\n" +
                "    \"workerReqDt\": \"2022-05-22T06:54:42.685777\"\n" +
                "  },\n" +
                "  \"errors\": {}\n" +
                "}";
    }
}
package com.codetest.szsrestapi.api.service.impl;

import com.codetest.szsrestapi.api.dto.request.ScrapReqDto;
import com.codetest.szsrestapi.api.dto.response.RefoundResDto;
import com.codetest.szsrestapi.api.entity.Scrap;
import com.codetest.szsrestapi.api.entity.ScrapHistory;
import com.codetest.szsrestapi.api.entity.User;
import com.codetest.szsrestapi.api.exception.ScrapApiException;
import com.codetest.szsrestapi.api.repository.ScrapHistoryRepository;
import com.codetest.szsrestapi.api.repository.ScrapRepository;
import com.codetest.szsrestapi.api.repository.UserRepository;
import com.codetest.szsrestapi.api.service.TaxService;
import com.codetest.szsrestapi.global.config.properties.SzsScrapProperties;
import com.codetest.szsrestapi.global.util.cipher.AES256Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TaxServiceImpl implements TaxService {
    private final AES256Util aes256Util;

    private final RestTemplate restTemplate;
    private final SzsScrapProperties scrapProperties;

    private final UserRepository userRepository;
    private final ScrapRepository scrapRepository;
    private final ScrapHistoryRepository scrapHistoryRepository;

    @Override
    @Transactional
    public Map<String, Object> scrap(User user, ResponseEntity<Object> apiResponse, ScrapHistory scrapHistory) {
        JSONObject body = new JSONObject(apiResponse).getJSONObject("body");

        if (body.getString("status").equals("fail"))
            throw new ScrapApiException(body.getJSONObject("errors").getString("message"));

        recordScrap(user, body, scrapHistory);

        return body.getJSONObject("data").toMap();
    }

    @Override
    @Transactional(propagation = Propagation.NEVER)
    public ResponseEntity<Object> callScrapApi(User user) {
        ResponseEntity<Object> apiResponse;

        try {
            apiResponse = restTemplate.postForEntity(
                    URI.create(scrapProperties.getScrapUrl())
                    , new ScrapReqDto(user.getName(), aes256Util.decrypt(user.getRegNo()))
                    , Object.class);
        } catch (HttpStatusCodeException e) {
            apiResponse = new ResponseEntity<Object>("", e.getStatusCode());
        }

        return apiResponse;
    }

    @Override
    @Transactional
    public ScrapHistory recordScrapHistory(User user, ResponseEntity<Object> apiResponse) {
        ScrapHistory scrapHistory = new ScrapHistory(user, apiResponse.getStatusCode().toString(), apiResponse.getBody().toString());

        return scrapHistoryRepository.save(scrapHistory);
    }

    @Transactional
    public void recordScrap(User user, JSONObject body, ScrapHistory scrapHistory) {
        double salary = 0, useAmount = 0;

        try {
            JSONObject object = body.getJSONObject("data").getJSONObject("jsonList");
            JSONObject scrap001 = object.getJSONArray("scrap001").getJSONObject(0);
            JSONObject scrap002 = object.getJSONArray("scrap002").getJSONObject(0);
            salary = Double.parseDouble(scrap001.getString("총지급액").replaceAll(",", ""));
            useAmount = Double.parseDouble(scrap002.getString("총사용금액").replaceAll(",", ""));
        } catch (JSONException e) {
            throw new ScrapApiException("스크랩 데이터 파싱 중 오류가 발생하였습니다");
        }

        Scrap scrap = new Scrap(user, salary, useAmount, scrapHistory);
        scrapRepository.save(scrap);
    }

    @Override
    public RefoundResDto refund(User user) {
        ScrapHistory scrapHistory = scrapHistoryRepository.findTopByUser(user).orElseThrow(
                () -> new IllegalStateException("스크랩 먼저 시도해주세요")
        );

        Scrap scrap = scrapRepository.findByUserAndScrapHistory(user, scrapHistory).orElseThrow(
                () -> new IllegalStateException("스크랩을 다시 시도해주세요")
        );

        double salary = scrap.getSalary(); // 총지급액
        double useAmount = scrap.getUseAmount(); // 총사용금액
        int limit = calcLimit(salary); // 한도
        int deductedAmount = calcDeductedAmount(useAmount); // 공제액
        int refundAmount = calcRefundAmount(limit, deductedAmount); // 환급액

        return new RefoundResDto(user.getName(), convertMoneyString(limit), convertMoneyString(deductedAmount), convertMoneyString(refundAmount));
    }

    public int calcRefundAmount(int limit, int deductedAmount) {
        int refundAmount = 0;

        refundAmount = Math.min(limit, deductedAmount);

        return refundAmount;
    }

    public int calcDeductedAmount(double useAmount) {
        double deductedAmount = 0;

        if (useAmount < 0)
            throw new IllegalArgumentException("총사용금액을 다시 확인해주세요");

        if (useAmount <= 1_300_000) {
            deductedAmount = (useAmount * 0.55);
        } else {
            deductedAmount = (715_000 + ((useAmount - 1_300_000) * 0.3));
        }

        return (int) deductedAmount;
    }

    public int calcLimit(double salary) {
        double calc = 0, limit = 0;

        if (salary < 0)
            throw new IllegalArgumentException("총지급액을 다시 확인해주세요");

        if (salary <= 33_000_000) {
            limit = 740_000;
        } else if (salary <= 70_000_000) {
            calc = (740_000 - ((salary - 33_000_000) * 0.008));
            limit = (calc < 660_000) ? 660_000 : calc;
        } else {
            calc = (660_000 - ((salary - 70_000_000) * 0.5));
            limit = (calc < 500_000) ? 500_000 : calc;
        }

        return (int) limit;
    }

    public String convertMoneyString(int input) {
        String[] han1 = {"", "십", "백", "천"};
        String[] han2 = {"", "만", "억"};

        StringBuilder sb = new StringBuilder();
        StringBuilder inputSb = new StringBuilder(String.valueOf(input));

        for (int i = 0; i < inputSb.length(); i++) {
            String s = String.valueOf(inputSb.charAt(i));

            if (!((inputSb.length() - i) % 4 == 0 && s.equals("0"))) {
                sb.append(s); // 4자리마다 맨앞이 0이면 무시
            }

            if ((inputSb.length() - (i + 1)) % 4 == 0) {
                sb.append(han2[(inputSb.length() - (i + 1)) / 4]); // 만, 억 단위 붙이기

                // 4자리마다 띄우는데 마지막은 안띄움
                if (inputSb.length() - 1 != i) {
                    sb.append(" ");
                }
            }
        }

        if (sb.lastIndexOf(" 000") == sb.length() - 4) {
            sb.delete(sb.length() - 4, sb.length());
        } else if (sb.lastIndexOf("000") == sb.length() - 3) {
            // 마지막이 000으로 떨어지면 "천"으로 변경
            sb.delete(sb.length() - 3, sb.length());
            sb.append(han1[3]);
        } else if (sb.length() == 3 || (String.valueOf(sb.charAt(sb.length() - 4)).equals(" ") && sb.lastIndexOf("00") == sb.length() - 2)) {
            // 백의자리로 끝나면 "백"으로 변경(천의자리가 있으면 변경하지 않는다) ex) 500 -> 5백, 4500 -> 4500
            sb.delete(sb.length() - 2, sb.length());
            sb.append(han1[2]);
        }

        sb.append("원");
        return sb.toString();
    }

    public User findUserIdFromAuth() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUserId(userId).orElseThrow(
                () -> new IllegalArgumentException("가입되지 않은 ID입니다")
        );
    }
}

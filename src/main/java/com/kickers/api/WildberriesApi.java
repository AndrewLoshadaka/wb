package com.kickers.api;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class WildberriesApi {

    private final RestTemplate restTemplate;
    public FeedbacksInfo getFeedbacks(String token, int count, int countSkip, String dateFrom, String dateTo) {
        String urlTemplate = UriComponentsBuilder.fromHttpUrl("https://feedbacks-api.wb.ru/api/v1/feedbacks")
                .queryParam("isAnswered", false)
                .queryParam("take", 5000)
                .queryParam("skip", countSkip * 5000)
                .queryParam("dateFrom", dateFrom)
                .queryParam("dateTo", dateTo)
                .encode()
                .toUriString();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);


        ResponseEntity<FeedbacksInfo> tempResult = restTemplate.exchange(urlTemplate, HttpMethod.GET,
                entity, FeedbacksInfo.class, count);


        return tempResult.getBody();
    }

    public int getUnansweredFeedbacksCount(String token) {
        String urlTemplate = UriComponentsBuilder.fromHttpUrl("https://feedbacks-api.wb.ru/api/v1/feedbacks/count-unanswered")
                .encode()
                .toUriString();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<UnansweredCount> result = restTemplate.exchange(urlTemplate, HttpMethod.GET,
                entity, UnansweredCount.class);

        return result.getBody().getData().getCountUnanswered();
    }

    public void answerOnFeedback(String token, String id, String text){
        String url = UriComponentsBuilder.fromHttpUrl("https://feedbacks-api.wb.ru/api/v1/feedbacks")
                .encode()
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        RequestBody requestBody = new RequestBody();
        requestBody.setId(id);
        requestBody.setText(text);

        HttpEntity<RequestBody> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.PATCH, entity, String.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            System.out.println("PATCH-запрос успешно отправлен.");
        } else {
            System.err.println("Ошибка при отправке PATCH-запроса. Код ошибки: " + responseEntity.getStatusCodeValue());
        }
    }

    public LinkedHashMap<String, String> getFeedbackById(String id, String token){
        String urlTemplate = UriComponentsBuilder.fromHttpUrl("https://feedbacks-api.wb.ru/api/v1/feedback")
                .queryParam("id", id)
                .encode()
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<LinkedHashMap> result = restTemplate.exchange(urlTemplate, HttpMethod.GET,
                entity, LinkedHashMap.class);
        return (LinkedHashMap<String, String>) result.getBody().get("data");
    }

    @Setter
    public static class RequestBody{
        private String id;
        private String text;
    }
    
}

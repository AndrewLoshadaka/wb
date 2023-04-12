package com.kickers.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WildberriesApi {

    private final RestTemplate restTemplate;
    public FeedbacksInfo getFeedbacks(String token, int count) {
        String urlTemplate = UriComponentsBuilder.fromHttpUrl("https://feedbacks-api.wb.ru/api/v1/feedbacks")
                .queryParam("isAnswered", false)
                .queryParam("take", "{take}")
                .queryParam("skip", 0)
                .encode()
                .toUriString();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<FeedbacksInfo> result = restTemplate.exchange(urlTemplate, HttpMethod.GET,
                entity, FeedbacksInfo.class, count);
        return result.getBody();
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
}

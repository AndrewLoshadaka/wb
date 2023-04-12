package com.kickers.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kickers.api.Feedback;
import com.kickers.api.WildberriesApi;
import com.kickers.entity.FeedbackEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class FeedbacksService {
    private final WildberriesApi wildberriesApi;
    private final ObjectMapper objectMapper;

    public List<Feedback> getFeedbacks(boolean photos, int stars) {
        List<String> tokens = List.of("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhY2Nlc3NJRCI6IjBkYzhjMjE4LTY5MjMtNDk0MS1iZmU3LTY0Mzg1OWVlNmYzZiJ9.ZH2zSk_SjPzXi2uTsBcmAFl70HZjd8JgK4lfXbYLVJI");
        List<CompletableFuture<List<Feedback>>> futures = new ArrayList<>();
        for (String token : tokens) {
            futures.add(CompletableFuture.supplyAsync(() -> getFeedbacks(token)));
        }
        List<FeedbackEntity> feedbacks = futures.stream()
                .map(CompletableFuture::join)
                .flatMap(Collection::stream)
                .map(v -> {
                    try {
                        return new FeedbackEntity(v.getId(), objectMapper.writeValueAsString(v));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();

        return feedbacks.stream()
                .map(v -> {
                    try {
                        return objectMapper.readValue(v.getFeedback(), Feedback.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(v -> v.getProductValuation() == stars)
                .filter(v -> !CollectionUtils.isEmpty(v.getPhotoLinks()) == photos)
                .toList();
    }

    private List<Feedback> getFeedbacks(String token) {
        int unansweredFeedbacksCount = wildberriesApi.getUnansweredFeedbacksCount(token);
        return wildberriesApi.getFeedbacks(token, unansweredFeedbacksCount).getData().getFeedbacks();
    }
}

package com.kickers.controller;

import com.kickers.api.Feedback;
import com.kickers.service.FeedbacksService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/api/feedbacks")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbacksService service;

    @GetMapping
    public List<Feedback> getFeedbacks(@RequestParam(value = "photos", defaultValue = "true") boolean photos,
                                       @RequestParam(value = "stars", defaultValue = "5") int stars) {
        return service.getFeedbacks(photos, stars);
    }
}

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
    public List<Feedback> getFeedbacks(@RequestParam(value = "photos", defaultValue = "") String photos,
                                       @RequestParam(value = "stars", defaultValue = "") String stars,
                                       @RequestParam(value = "video", defaultValue = "") String video,
                                       @RequestParam(value = "name", defaultValue = "") String supplier) {
        return service.getFeedbacks(photos, stars, supplier, video);
    }
}

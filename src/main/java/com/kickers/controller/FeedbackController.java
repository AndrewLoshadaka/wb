package com.kickers.controller;

import com.kickers.api.Feedback;
import com.kickers.dao.FeedbackDto;
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
                                       @RequestParam(value = "brand", defaultValue = "") String brand,
                                       @RequestParam(value = "video", defaultValue = "") String video) {
        return service.getAllFeedbacks(photos, stars, video, brand);
    }

    @GetMapping("/empty")
    public List<FeedbackDto> getEmptyFeedbackList(
            @RequestParam(value = "date") long date){
        return service.getAutoAnswerFeedbacks(date);
    }
}

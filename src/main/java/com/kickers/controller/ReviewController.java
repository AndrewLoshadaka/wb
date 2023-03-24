package com.kickers.controller;

import com.kickers.entity.ReviewEntity;
import com.kickers.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping()
    public List<ReviewEntity> getAllReview() {
        return reviewService.getAllReviews();
    }
}

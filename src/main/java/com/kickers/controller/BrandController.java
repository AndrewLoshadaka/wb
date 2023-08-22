package com.kickers.controller;

import com.kickers.api.Feedback;
import com.kickers.service.FeedbacksService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController()
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class BrandController {
    private final FeedbacksService service;
    @GetMapping
    public Set<String> getFeedbacks(@RequestParam(value = "name", defaultValue = "") String supplier) {
        return service.getBrandName(supplier);
    }
}

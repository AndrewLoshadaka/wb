package com.kickers.controller;

import com.kickers.api.Feedback;
import com.kickers.dao.AnswerDto;
import com.kickers.service.FeedbacksService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController()
@RequestMapping("/api/feedbacks")
@RequiredArgsConstructor
public class FeedbackController {
    private final FeedbacksService service;
    @GetMapping
    public List<Feedback> getFeedbacks(@RequestParam(value = "photos", defaultValue = "") String photos,
                                       @RequestParam(value = "stars", defaultValue = "") String stars,
                                       @RequestParam(value = "brand", defaultValue = "") String brand,
                                       @RequestParam(value = "video", defaultValue = "") String video,
                                       @RequestParam(value = "name", defaultValue = "") String supplier) {
        return service.getFeedbacksSort(photos, stars, supplier, video, brand);
    }



    @PostMapping("/answer")
    public ResponseEntity<String> sendResponse(@RequestBody AnswerDto body){
        try{
            System.out.println(body.getId() + " " + body.getText() + " " + body.getBrandName());
            return ResponseEntity.ok("{\"message\": \"ok!\"}");
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"error!\"}");
        }
    }
}

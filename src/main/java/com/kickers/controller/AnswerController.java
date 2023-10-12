package com.kickers.controller;

import com.kickers.dao.AnswerDto;
import com.kickers.service.AnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/feedbacks")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService = new AnswerService();

    @PostMapping("/answer")
    public ResponseEntity<Map<String, String>> getAnswer(
            @RequestBody Map<String, String> map){
        String answer = answerService.createAnswer(map.get("product"), map.get("brand"), map.get("supplier"));
        Map<String, String> response = new HashMap<>();
        response.put("answer", answer);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/send-answer")
    public ResponseEntity<String> sendResponse(@RequestBody AnswerDto body){
        try{
            System.out.println(body.getText());
            return ResponseEntity.ok("{\"message\": \"ok!\"}");
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"error!\"}");
        }
    }

}

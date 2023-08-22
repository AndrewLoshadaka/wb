package com.kickers.controller;

import com.kickers.dao.AnswerDto;
import com.kickers.service.AnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/feedbacks")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService = new AnswerService();

    @GetMapping("/answer")
    private ResponseEntity<Map<String, String>> getAnswer(){
        String answer = answerService.createAnswer();
        Map<String, String> response = new HashMap<>();
        response.put("answer", answer);
        return ResponseEntity.ok(response);
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

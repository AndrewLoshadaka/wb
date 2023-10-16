package com.kickers.service;

import com.kickers.api.WildberriesApi;
import com.kickers.dao.AnswerDAO;
import com.kickers.dao.ConnectionDB;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.*;
import java.util.*;


@Service
public class AnswerService {
    private final Connection connection = ConnectionDB.getConnection();
    private final WildberriesApi wildberriesApi = new WildberriesApi(new RestTemplate());
    private final AnswerDAO answerDAO = new AnswerDAO();
    public AnswerService() throws SQLException {
    }

    public String createAnswer(String template, String brand, String supplier, String photo) {
        return answerDAO.createAnswer(template, brand, supplier, photo);
    }

    public List<AnswerDAO.Template> getAnswerByTemplate(){
        return answerDAO.getAnswerByTemplate();
    }
    public boolean getStateFeedback(String id, String supplier){
        String apiToken = answerDAO.getMapSuppliersToken().get(supplier).getToken();
        LinkedHashMap<String, String> feedback = wildberriesApi.getFeedbackById(id, apiToken);

        return feedback.get("state").equals("none");
    }


    @Getter
    @Setter
    @AllArgsConstructor
    public static class Supplier {
        private String name;
        private String token;
    }
}

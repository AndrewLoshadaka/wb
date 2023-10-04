package com.kickers.service;

import com.kickers.api.WildberriesApi;
import com.kickers.dao.ConnectionDB;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;


@Service
public class AnswerService {
    private final Connection connection = ConnectionDB.getConnection();

    private final WildberriesApi wildberriesApi = new WildberriesApi(new RestTemplate());

    private List<String> regexString = new ArrayList<>();
    {
        regexString.add("#product#");
        regexString.add("#im_product#");
    }
    public AnswerService() {
    }



    public String createAnswer(String template, String brand, String supplier) {
        String answer = "";

        for(int i = 0; i < 5; i++) {
            answer += getPartsAnswer(i, getMapSuppliersToken().get(supplier).getName()) + " ";
        }
        for(String s : regexString){
            answer = answer.replaceAll(s, Objects.requireNonNull(findAnswerOnDataBase(template, s)));
        }

        answer = answer.replaceAll("#brand#", brand);
        answer += "С уважением, " + brand + ".";

        return answer;
    }



    private String getPartsAnswer(int numberPart, String supplier){
        String answerPart = null;
        try{
            assert connection != null;
            String query = "select part_" + numberPart + " from answer_sample where corp_name='" + supplier + "'";
            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(query);

            List<String> list = new ArrayList<>();
            while (set.next() && set.getString("part_" + numberPart) != null){
                list.add(set.getString("part_" + numberPart));
            }

            answerPart = list.get(
                    (int) (Math.random() * (list.size()) - 1)
            );

        }catch (SQLException e){
            e.printStackTrace();
        }

        return answerPart;
    }

    public boolean getStateFeedback(String id, String supplier){
        String apiToken = getMapSuppliersToken().get(supplier).getToken();
        LinkedHashMap<String, String> feedback = wildberriesApi.getFeedbackById(id, apiToken);

        return feedback.get("state").equals("none");
    }

    // TODO: 03.10.2023 храним в формате: full_name => {corp_name, token}
    private Map<String, Supplier> getMapSuppliersToken(){
        Map<String, Supplier> mapSuppliersToken = new HashMap<>();
        List<String> suppliersList = new ArrayList<>();
        suppliersList.add("ОБЩЕСТВО С ОГРАНИЧЕННОЙ ОТВЕТСТВЕННОСТЬЮ \"АЛЛЮР\"");
        suppliersList.add("ОБЩЕСТВО С ОГРАНИЧЕННОЙ ОТВЕТСТВЕННОСТЬЮ \"ВИКТОРИАС ЛАЙН\"");
        suppliersList.add("Павлова Марина Алексеевна ИП");
        suppliersList.add("ОБЩЕСТВО С ОГРАНИЧЕННОЙ ОТВЕТСТВЕННОСТЬЮ \"ИН ТАЙМ\"");
        suppliersList.add("ИП Прялков Олег Александрович");

        try {
            assert connection != null;
            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery("select * from corps");
            int iterator = 0;
            while (set.next()){
                Supplier tempSupplier = new Supplier(set.getString(1), set.getString(11));
                mapSuppliersToken.put(suppliersList.get(iterator), tempSupplier);
                iterator++;
            }

        } catch (SQLException e){
            e.getSQLState();
        }

        return mapSuppliersToken;
    }

    private String findAnswerOnDataBase(String template, String regex){
        String answer = null;
        try {
            assert connection != null;
            Statement statement = connection.createStatement();
            ResultSet resultSet;
            switch (regex){
                //case "#product#" -> resultSet = statement.executeQuery("select answer from answer_template where template = '" + template + "'");
                case "#im_product#" -> resultSet = statement.executeQuery("select im_answer as answer from answer_template where template = '" + template + "'");

                //#product#
                default -> resultSet = statement.executeQuery("select answer_brand as answer from answer_template where template = '" + template + "'");
            }
            if(resultSet.next()){
                answer = resultSet.getString("answer");
                if(answer == null){
                    return null;
                }
            } else {
                String query = "insert into answer_template (template, answer) value('" + template + "', null, null, null, null)";
                statement.executeUpdate(query);
                return null;
            }
        } catch (SQLException ignored) {
        }

        return answer;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Supplier {
        private String name;
        private String token;
    }
}

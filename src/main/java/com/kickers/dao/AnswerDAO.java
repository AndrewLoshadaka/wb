package com.kickers.dao;

import com.kickers.service.AnswerService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class AnswerDAO {
    private final Connection connection = ConnectionDB.getConnection();
    private final Map<String, Supplier> supplierMap = getMapSuppliersToken();

    public AnswerDAO() throws SQLException {
    }

    public String createAnswer(String template, String brand, String supplier, String photo) {
        String answer = "";

        for(int i = 0; i < getCountParts(photo); i++) {
            answer += getPartsAnswer(i, supplierMap.get(supplier).getName()) + " ";
        }
        for(String regexName : getAllRegexString()){
            if(findAnswerOnDataBase(template, regexName) != null)
                answer = answer.replaceAll(regexName, Objects.requireNonNull(findAnswerOnDataBase(template, regexName)));
        }

        answer = answer.replaceAll("#brand#", brand);
        answer += "С уважением, " + brand + ".";

        return answer;
    }

    public List<Template> getAnswerByTemplate(){
        String query = "select * from answer_template";
        List<Template> templateList = new ArrayList<>();
        try {
            Statement answerStatement = connection.createStatement();
            ResultSet set = answerStatement.executeQuery(query);
            while(set.next()){
                templateList.add(new Template(set.getString("template"), set.getString("answer")));
            }
            set.close();
        } catch (SQLException sqlException){
            sqlException.printStackTrace();
        }

        return templateList;
    }

    private int getCountParts(String photo){
        int result = 0;
        String query = "select count(column_name) as result from information_schema.columns " +
                "where table_name = 'answer_sample'";
        try{
            assert connection != null;
            Statement answerStatement = connection.createStatement();
            ResultSet set = answerStatement.executeQuery(query);
            set.next();
            if(photo.equals("true"))
                result = set.getInt("result") - 2;
            else if(photo.equals("false"))
                result = set.getInt("result") - 3;

            set.close();
        } catch (SQLException e){
            e.printStackTrace();
        }

        return result;
    }

    //получаем все регулярные выражения
    private List<String> getAllRegexString(){
        List<String> regexString = new ArrayList<>();
        String query = "select column_name from information_schema.columns " +
                "where table_name = 'answer_regex'";
        try{
            Statement answerStatement = connection.createStatement();
            ResultSet set = answerStatement.executeQuery(query);
            while(set.next()){
                regexString.add(set.getString("COLUMN_NAME"));
            }

            regexString.remove(0);
            regexString.remove(0);
        } catch (SQLException e){
            e.printStackTrace();
        }
        return regexString;
    }
    // TODO: 03.10.2023 храним в формате: full_name => {corp_name, token}
    public Map<String, Supplier> getMapSuppliersToken(){
        Map<String, Supplier> mapSuppliersToken = new HashMap<>();
        List<String> suppliersList = new ArrayList<>();
        suppliersList.add("ОБЩЕСТВО С ОГРАНИЧЕННОЙ ОТВЕТСТВЕННОСТЬЮ \"АЛЛЮР\"");
        suppliersList.add("ОБЩЕСТВО С ОГРАНИЧЕННОЙ ОТВЕТСТВЕННОСТЬЮ \"ВИКТОРИАС ЛАЙН\"");
        suppliersList.add("Павлова Марина Алексеевна ИП");
        suppliersList.add("ОБЩЕСТВО С ОГРАНИЧЕННОЙ ОТВЕТСТВЕННОСТЬЮ \"ИН ТАЙМ\"");
        suppliersList.add("ИП Прялков Олег Александрович");

        try {
            assert connection != null;
            String query = "select * from corps";
            Statement answerStatement = connection.createStatement();
            ResultSet set = answerStatement.executeQuery(query);
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

    private String getPartsAnswer(int numberPart, String supplier){
        String answerPart = null;
        try{
            assert connection != null;
            String query = "select part_" + numberPart + " from answer_sample where corp_name='" + supplier + "'";
            Statement answerStatement = connection.createStatement();
            ResultSet set = answerStatement.executeQuery(query);
            List<String> list = new ArrayList<>();
            while (set.next() && set.getString("part_" + numberPart) != null){
                list.add(set.getString("part_" + numberPart));
            }

            answerPart = list.get(
                    (int) (Math.random() * (list.size()) - 1)
            );
            set.close();
        }catch (SQLException e){
            e.printStackTrace();
        }

        return answerPart;
    }

    private String findAnswerOnDataBase(String template, String regex){
        String answer = null;
        try {
            assert connection != null;
            String query = "select `" +  regex + "` as answer from answer_regex where template = '" + template + "'";
            Statement answerStatement = connection.createStatement();
            ResultSet set = answerStatement.executeQuery(query);
            if(set.next()){
                answer = set.getString("answer");
                if(answer == null){
                    return null;
                }
            } else {
                String queryInsert = "insert into answer_regex (template, answer) value('" + template + "', null, null, null, null, null)";
                answerStatement.executeUpdate(queryInsert);
                return null;
            }
        } catch (SQLException ignored) {
        }
        return answer;
    }
    @Getter
    @Setter
    @AllArgsConstructor
    public static class Template {
        private String template;
        private String answer;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Supplier {
        private String name;
        private String token;
    }
}

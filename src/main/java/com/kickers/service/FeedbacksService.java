package com.kickers.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kickers.api.Feedback;
import com.kickers.api.ProductDetails;
import com.kickers.api.WildberriesApi;
import com.kickers.dao.ConnectionDB;
import com.kickers.entity.FeedbackEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class FeedbacksService {
    private final Connection connection = ConnectionDB.getConnection();
    private final WildberriesApi wildberriesApi;
    private final ObjectMapper objectMapper;


    private String getToken(String supplier){
        try {
            assert connection != null;
            PreparedStatement statement = connection.prepareStatement("select API_NEW as a from corps where corpname =" + "\'" + supplier + "\'");
            ResultSet result = statement.executeQuery();
            result.next();
            return result.getString("a");
        } catch (SQLException e){}
        return null;
    }

    private List<String> getAllCorpName(){
        List<String> list = new ArrayList<>();
        try {
            assert connection != null;
            PreparedStatement statement = connection.prepareStatement("select corpname as name from corps");
            ResultSet result = statement.executeQuery();
            while (result.next()){
                list.add(result.getString("name"));
            }
            return list;
        } catch (SQLException e){}
        return null;
    }

    public List<Feedback> getFeedbacks(String photos, String stars, String supplier, String video) {
        List<Feedback> feedbacks;
        List<FeedbackEntity> feedbackList;
        if(supplier.equals("")){
            List<String> tokens = new ArrayList<>();
            for(String s : Objects.requireNonNull(getAllCorpName()))
                tokens.add(getToken(s));
            List<CompletableFuture<List<Feedback>>> futures = new ArrayList<>();
            for (String token : tokens) {
                futures.add(CompletableFuture.supplyAsync(() -> getFeedbacks(token)));
            }
            feedbackList = futures.stream()
                    .map(CompletableFuture::join)
                    .flatMap(Collection::stream)
                    .map(v -> {
                        try {
                            return new FeedbackEntity(v.getId(), objectMapper.writeValueAsString(v));
                        } catch(JsonProcessingException e){
                            throw new RuntimeException(e);
                        }
                    }).toList();
            feedbacks = feedbackList.stream()
                    .map(v -> {
                        try{
                            return objectMapper.readValue(v.getFeedback(), Feedback.class);
                        } catch (JsonProcessingException e){
                            throw new RuntimeException(e);
                        }
                    }).toList();


        }

        else {
            feedbacks = getFeedbacks(Objects.requireNonNull(getToken(supplier)));
            feedbackList = feedbacks.stream()
                    .map(v -> {
                        try {
                            return new FeedbackEntity(v.getId(), objectMapper.writeValueAsString(v));
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }).toList();
        }



        if(!stars.equals("")){
            feedbacks = feedbackList.stream()
                    .map(v -> {
                        try{
                            return objectMapper.readValue(v.getFeedback(), Feedback.class);
                        }
                        catch (JsonProcessingException e){
                            throw new RuntimeException(e);
                        }
                    })
                    .filter(v -> v.getProductValuation() == Integer.parseInt(stars))
                    .toList();
        }

        if(!photos.equals("")){
            feedbacks = feedbacks.stream()
                    .filter(v -> !CollectionUtils.isEmpty(v.getPhotoLinks()) == Boolean.parseBoolean(photos))
                    .toList();
        }

        if(!video.equals("")){
            feedbacks = feedbacks.stream()
                    .filter(v -> v.getVideo() != null)
                    .toList();
        }

        for(Feedback x : feedbacks){
            ProductDetails tempProductDetails = x.getProductDetails();
            tempProductDetails.setPhotoLink(getPhotoLink(tempProductDetails.getNmId()));
            tempProductDetails.setWbUrl("https://www.wildberries.ru/catalog/" + tempProductDetails.getNmId() +"/detail.aspx");
            x.setProductDetails(tempProductDetails);
        }
        return feedbacks;
    }


    private String getPhotoLink(long nmId){
        String nmIdStr = String.valueOf(nmId);
        String result = null;
        if(nmId > 131_200_000)
            result = "https://basket-10.wb.ru/vol" + nmIdStr.substring(0, 4) + "/part" //https://basket-10.wb.ru/vol176/part/17665/17665257/images/c246x328/1.jpg
                    + nmIdStr.substring(0, 6) + "/" + nmIdStr + "/images/c246x328/1.jpg";
        else if(nmId > 116_900_000)
            result = "https://basket-09.wb.ru/vol" + nmIdStr.substring(0, 4) + "/part"
                    + nmIdStr.substring(0, 6) + "/" + nmIdStr + "/images/c246x328/1.jpg";
        else if(nmId > 111_500_000)
            result = "https://basket-08.wb.ru/vol" + nmIdStr.substring(0, 4) + "/part"
                    + nmIdStr.substring(0, 6) + "/" + nmIdStr + "/images/c246x328/1.jpg";
        else if(nmId > 106_100_000)
            result = "https://basket-07.wb.ru/vol" + nmIdStr.substring(0, 4) + "/part"
                    + nmIdStr.substring(0, 6) + "/" + nmIdStr + "/images/c246x328/1.jpg";
        else if(nmId > 100_700_000)
            result = "https://basket-06.wb.ru/vol" + nmIdStr.substring(0, 4) + "/part"
                    + nmIdStr.substring(0, 6) + "/" + nmIdStr + "/images/c246x328/1.jpg";
        else if(nmId > 99_999_999)
            result = "https://basket-05.wb.ru/vol" + nmIdStr.substring(0, 4) + "/part"
                    + nmIdStr.substring(0, 6) + "/" + nmIdStr + "/images/c246x328/1.jpg";
        else if(nmId > 71_900_000)
            result = "https://basket-05.wb.ru/vol" + nmIdStr.substring(0, 3) + "/part"
                    + nmIdStr.substring(0, 5) + "/" + nmIdStr + "/images/c246x328/1.jpg";
        else if(nmId > 43_100_000)
            result = "https://basket-04.wb.ru/vol" + nmIdStr.substring(0, 3) + "/part"
                    + nmIdStr.substring(0, 5) + "/" + nmIdStr + "/images/c246x328/1.jpg";
        else if(nmId > 28_700_000)
            result = "https://basket-03.wb.ru/vol" + nmIdStr.substring(0, 3) + "/part"
                    + nmIdStr.substring(0, 5) + "/" + nmIdStr + "/images/c246x328/1.jpg";
        else if(nmId > 14_300_000)
            result = "https://basket-02.wb.ru/vol" + nmIdStr.substring(0, 3) + "/part" //https://basket-02.wb.ru/vol277/part27765/27765179/images/tm/1.jpg
                    + nmIdStr.substring(0, 5) + "/" + nmIdStr + "/images/c246x328/1.jpg";
        else if(nmId > 10_000_000)
            result = "https://basket-01.wb.ru/vol" + nmIdStr.substring(0, 3) + "/part"
                    + nmIdStr.substring(0, 5) + "/" + nmIdStr + "/images/c246x328/1.jpg";
        else if(nmId > 1_000_000)
            result = "https://basket-01.wb.ru/vol" + nmIdStr.substring(0, 2) + "/part"
                    + nmIdStr.substring(0, 4) + "/" + nmIdStr + "/images/c246x328/1.jpg";

        return result;
    }

    private List<Feedback> getFeedbacks(String token) {
        int unansweredFeedbacksCount = wildberriesApi.getUnansweredFeedbacksCount(token);
        return wildberriesApi.getFeedbacks(token, unansweredFeedbacksCount).getData().getFeedbacks();
    }
}

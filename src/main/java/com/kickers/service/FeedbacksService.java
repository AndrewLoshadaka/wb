package com.kickers.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kickers.api.Feedback;
import com.kickers.api.ProductDetails;
import com.kickers.api.WildberriesApi;
import com.kickers.dao.ConnectionDB;
import com.kickers.entity.FeedbackEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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
    private List<Feedback> feedbacks = new ArrayList<>();
    private List<Feedback> feedbacksCopy = new ArrayList<>(); //копия для сортировки

    private final State state = new State();


    private String getToken(String supplier){
        try {
            assert connection != null;
            PreparedStatement statement = connection.prepareStatement("select API_NEW as a from corp where corpname =" + "\'" + supplier + "\'");
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
            PreparedStatement statement = connection.prepareStatement("select corpname as name from corp");
            ResultSet result = statement.executeQuery();
            while (result.next()){
                list.add(result.getString("name"));
            }
            return list;
        } catch (SQLException e){}
        return null;
    }

    public void getFeedbacks(String name){
        List<FeedbackEntity> feedbackList;
        if(name.equals("")){ //все
            List<String> tokens = new ArrayList<>();
            for(String s : Objects.requireNonNull(getAllCorpName()))
                tokens.add(getToken(s));
            List<CompletableFuture<List<Feedback>>> futures = new ArrayList<>();
            for (String token : tokens) {
                futures.add(CompletableFuture.supplyAsync(() -> getFeedbacksSort(token)));
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
        else
            feedbacks = getFeedbacksSort(getToken(name)); //по токену
        feedbacksCopy = feedbacks;
        System.out.println("size - " + feedbacks.size());
    }

    public Set<String> getBrandName(String name){
        Set<String> brandSet = new HashSet<>();
        getFeedbacks(name);
        state.setPhotos("p");
        state.setStars("p");
        for(Feedback x : feedbacks){
            brandSet.add(x.getProductDetails().getBrandName());
        }
        System.out.println(brandSet.size());
        return brandSet;
    }

    public List<Feedback> getFeedbacksSort(String photos, String stars, String supplier, String video, String brand) {
        //копия списка отзывов для сортировки по фильтрам
        System.out.println("copy size - " + feedbacksCopy.size());
        if(state.getStars().equals(stars) && state.getPhotos().equals(photos) && state.getBrand().equals(brand) )
            return feedbacksCopy;

        else {
            feedbacksCopy = feedbacks;
            List<FeedbackEntity> feedbackList;
            feedbackList = feedbacksCopy.stream()
                    .map(v -> {
                        try {
                            return new FeedbackEntity(v.getId(), objectMapper.writeValueAsString(v));
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }).toList();

            if (!stars.equals("")) {
                feedbacksCopy = feedbackList.stream()
                        .map(v -> {
                            try {
                                return objectMapper.readValue(v.getFeedback(), Feedback.class);
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .filter(v -> v.getProductValuation() == Integer.parseInt(stars))
                        .toList();
            }

            if (!photos.equals("")) {
                feedbacksCopy = feedbacksCopy.stream()
                        .filter(v -> !CollectionUtils.isEmpty(v.getPhotoLinks()) == Boolean.parseBoolean(photos))
                        .toList();
            }

            if (!video.equals("")) {
                feedbacksCopy = feedbacksCopy.stream()
                        .filter(v -> v.getVideo() != null)
                        .toList();
            }

            if(!brand.equals("")){
                feedbacksCopy = feedbacksCopy.stream()
                        .filter(v -> v.getProductDetails().getBrandName().equals(brand))
                        .toList();
            }

            for (Feedback x : feedbacksCopy) {
                ProductDetails tempProductDetails = x.getProductDetails();
                tempProductDetails.setPhotoLink(getPhotoLink(tempProductDetails.getNmId()));
                tempProductDetails.setWbUrl("https://www.wildberries.ru/catalog/" + tempProductDetails.getNmId() + "/detail.aspx");
                x.setProductDetails(tempProductDetails);
            }
            state.setStars(stars);
            state.setPhotos(photos);
            state.setBrand(brand);
            System.out.println("get list!");
            return feedbacksCopy;
        }
    }

    private String getPhotoLink(long nmId){
        String nmIdStr = String.valueOf(nmId);
        String result = null;
        if(nmId > 131_200_000)
            result = "https://basket-10.wb.ru/vol" + nmIdStr.substring(0, 4) + "/part"
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

    private List<Feedback> getFeedbacksSort(String token) {
        int unansweredFeedbacksCount = wildberriesApi.getUnansweredFeedbacksCount(token);
        return wildberriesApi.getFeedbacks(token, unansweredFeedbacksCount).getData().getFeedbacks();
    }

    @Getter
    @Setter
    public class State{
        private String stars;
        private String photos;
        private String brand;
    }
}

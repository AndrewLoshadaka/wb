package com.kickers.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kickers.api.Feedback;
import com.kickers.api.ProductDetails;
import com.kickers.api.WildberriesApi;
import com.kickers.dao.ConnectionDB;
import com.kickers.dao.FeedbackDto;
import com.kickers.entity.FeedbackEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class FeedbacksService {
    private final Connection connection = ConnectionDB.getConnection();
    private final WildberriesApi wildberriesApi;
    private final ObjectMapper objectMapper;
    private List<Feedback> feedbacks = new ArrayList<>();
    private List<Feedback> autoAnswerFeedbacks = new ArrayList<>(); //
    private List<Feedback> feedbacksCopy = new ArrayList<>(); //копия для сортировки

    private final State state = new State();


    private String getToken(String supplier){
        try {
            assert connection != null;
            PreparedStatement statement = connection.prepareStatement("select API_NEW as a from corps where corpname =" + "'" + supplier + "'");
            ResultSet result = statement.executeQuery();
            result.next();
            System.out.println(result.getString("a"));
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

    // TODO: 15.09.2023 логику получения всех отзывов вынести в отдельный класс
    // TODO: 15.09.2023 после этого сортировать отзывы для автоответа и для отправки в json на фронт

    public List<FeedbackDto> getAutoAnswerFeedbacks(long startDate){
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String formattedDate = currentDate.format(dateFormatter);
        long timestampCurrent = 0;
        String name = "";

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        try {
            Date date = sdf.parse(formattedDate);
            timestampCurrent =  date.getTime() / 1000; //current day
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long timestampLastWeek = timestampCurrent - 86400 * 7;
        getFeedbacks(name, String.valueOf(startDate), String.valueOf(timestampCurrent));

        List<FeedbackDto> detailsFeedbacksList = new ArrayList<>();
        for(Feedback x : autoAnswerFeedbacks){
            detailsFeedbacksList.add(new FeedbackDto(
                    String.valueOf(x.getProductDetails().getNmId()),
                    String.valueOf(x.getProductValuation()),
                    x.getProductDetails().getProductName(),
                    x.getProductDetails().getBrandName(),
                    x.getProductDetails().getSupplierName())
            );
        }

        return detailsFeedbacksList;
    }


    //должен возвращать вообще все
    private void getFeedbacks(String name, String dateFrom, String dateTo){
        List<FeedbackEntity> feedbackList;

        if(name.isEmpty()){ //все
            List<String> tokens = new ArrayList<>();
            for(String s : Objects.requireNonNull(getAllCorpName()))
                tokens.add(getToken(s));
            List<CompletableFuture<List<Feedback>>> futures = new ArrayList<>();
            for (String token : tokens) {
                futures.add(CompletableFuture.supplyAsync(() -> getAllFeedbacks(token, dateFrom, dateTo)));
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

            List<Feedback> temp = feedbackList.stream()
                    .map(v -> {
                        try{
                            return objectMapper.readValue(v.getFeedback(), Feedback.class);
                        } catch (JsonProcessingException e){
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();

            splitFeedbacks(temp);
            System.out.println(temp.size() + " " + autoAnswerFeedbacks.size() + " " + feedbacks.size() + " " + (autoAnswerFeedbacks.size() + feedbacks.size()));
        }
        else {
            List<Feedback> temp = getAllFeedbacks(getToken(name), dateFrom, dateTo);
            splitFeedbacks(temp);
            System.out.println(temp.size() + " " + autoAnswerFeedbacks.size() + " " + feedbacks.size() + " " + (autoAnswerFeedbacks.size() + feedbacks.size()));
        }

        feedbacksCopy = feedbacks;
    }



    private void splitFeedbacks(List<Feedback> temp){
        autoAnswerFeedbacks = temp.stream()
                .filter(v -> v.getPhotoLinks() == null)
                .filter(v -> v.getProductValuation() == 4 || v.getProductValuation() == 5)
                .filter(v -> v.getText().isEmpty())
                .filter(v -> v.getVideo() == null)
                .toList();
        feedbacks = temp.stream()
                .filter(item -> !autoAnswerFeedbacks.contains(item))
                .toList();
    }

    public Set<String> getBrandName(String name, String dateFrom, String dateTo){
        Set<String> brandSet = new HashSet<>();
        getFeedbacks(name, dateFrom, dateTo);
        state.setPhotos("p");
        state.setStars(new String[0]);
        state.setVideo("p");
        for(Feedback x : feedbacks){
            brandSet.add(x.getProductDetails().getBrandName());
        }
        System.out.println("size brandset " + brandSet.size());
        return brandSet;
    }

    public List<Feedback> getAllFeedbacks(String photos, String[] stars, String video, String brand, String text) {
        /*if(state.getStars().equals(stars)
                && state.getPhotos().equals(photos)
                && state.getBrand().equals(brand)
                && state.getVideo().equals(video))
            return feedbacksCopy;*/

        //else {
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

            if (stars.length != 0) {
                feedbacksCopy = feedbackList.stream()
                        .map(v -> {
                            try {
                                return objectMapper.readValue(v.getFeedback(), Feedback.class);
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .filter(v -> Arrays.stream(stars).anyMatch(star -> v.getProductValuation() == Integer.parseInt(star)))
                        .toList();
            }

            if (!photos.isEmpty()) {
                feedbacksCopy = feedbacksCopy.stream()
                        .filter(v -> !CollectionUtils.isEmpty(v.getPhotoLinks()) == Boolean.parseBoolean(photos))
                        .toList();
            }

            if (video.equals("true")) {
                System.out.println(feedbacksCopy.size());
                feedbacksCopy = feedbacksCopy.stream()
                        .filter(v -> v.getVideo() != null)
                        .toList();
                System.out.println(feedbacksCopy.size());
            } else if (video.equals("false")) {
                System.out.println(feedbacksCopy.size());
                feedbacksCopy = feedbacksCopy.stream()
                        .filter(v -> v.getVideo() == null)
                        .toList();
                System.out.println(feedbacksCopy.size());
            }

            if(text.equals("true")){
                feedbacksCopy = feedbacksCopy.stream()
                        .filter(v -> !v.getText().isEmpty())
                        .toList();
            } else if (text.equals("false")) {
                feedbacksCopy = feedbacksCopy.stream()
                        .filter(v -> v.getText().isEmpty())
                        .toList();
            }


        if(!brand.isEmpty()){
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
            state.setVideo(video);
            return feedbacksCopy;
        }
    private String getPhotoLink(long nmId){
        String nmIdStr = String.valueOf(nmId);
        String result = null;
        if(nmId > 160_610_000){
            result = "https://basket-11.wb.ru/vol" + nmIdStr.substring(0, 4) + "/part"
                    + nmIdStr.substring(0, 6) + "/" + nmIdStr + "/images/c246x328/1.jpg";
        }
        else if(nmId > 131_200_000)
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

    private List<Feedback> getAllFeedbacks(String token, String dateFrom, String dateTo) {
        int unansweredFeedbacksCount = wildberriesApi.getUnansweredFeedbacksCount(token);

        int countSteps = unansweredFeedbacksCount / 5000 + 1;
        List<Feedback> tempList = new ArrayList<>();

        if (countSteps == 0) {
            tempList = wildberriesApi.getFeedbacks(token, unansweredFeedbacksCount, 0,
                            dateFrom,
                            dateTo)
                    .getData()
                    .getFeedbacks();
        } else {
            for (int i = 0; i < countSteps; i++) {
                tempList.addAll(wildberriesApi.getFeedbacks(token, unansweredFeedbacksCount, i,
                                dateFrom,
                                dateTo)
                        .getData()
                        .getFeedbacks());
            }
        }
        return tempList;
    }

    @Getter
    @Setter
    public class State{
        private String[] stars;
        private String photos;
        private String brand;
        private String video;
    }
}

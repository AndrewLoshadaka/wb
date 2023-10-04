package com.kickers.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FeedbacksInfo {

    private Data data;
    private boolean error;
    private String errorText;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public class Data {
        private int countUnanswered;
        private int countArchive;

        private List<Feedback> feedbacks;
    }
}

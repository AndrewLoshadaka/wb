package com.kickers.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UnansweredCount {
    private Data data;
    private boolean error;
    private String errorText;
//              "additionalErrors": null

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public class Data {
        private int countUnanswered;
        private int countUnansweredToday;

        private String valuation;
    }
}

package com.kickers.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FeedbackDto {
    private String id;
    private String productValuation;
    private String productName;
    private String brandName;
    private String supplier;
}

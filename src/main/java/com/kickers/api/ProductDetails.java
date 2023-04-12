package com.kickers.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetails {
    private long imtId;
    private long nmId;
    private String productName;
    private String supplierArticle;
    private String supplierName;
    private String brandName;
}

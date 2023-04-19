package com.kickers.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.URI;
import java.net.URL;

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

    @Setter
    private String photoLink;
    @Setter
    private String wbUrl;
}

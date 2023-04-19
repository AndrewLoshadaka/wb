package com.kickers.api;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {

    private String id;
    private long imtId;
    private long nmId;
    private long subjectId;
    private String text;
    private int productValuation;
    private ZonedDateTime createdDate;
    private ZonedDateTime updatedDate;

    private Answer answer;
    private String state;
    @Setter
    private ProductDetails productDetails;

    private List<PhotoLink> photoLinks;
    private Video video;
    //            "video": null,
    private boolean isCreationSupplierComplaint;
    private boolean wasViewed;
//    private String supplierComplaint;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Answer {
        private String text;
        private String state;
        private boolean editable;

    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class PhotoLink {
        private String fullSize;
        private String minSize;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Video {
        private String uri;
        private String thumbnailUri;
    }
}

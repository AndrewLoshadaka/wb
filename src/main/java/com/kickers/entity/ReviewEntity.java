package com.kickers.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "reviews")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewEntity {
    @Id
    private long id;

    private String author;

    private String status;
}

package com.kickers.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "feedback")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackEntity {
    @Id
    private String id;

    private String feedback;
}

package com.fourspoons.mikkureomi.mealPicture.domain;

import com.fourspoons.mikkureomi.common.BaseTimeEntity;
import com.fourspoons.mikkureomi.meal.domain.Meal;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "meal_picture")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MealPicture extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meal_picture_id", nullable = false)
    private Long mealPictureId;

    // Meal과의 일대일 관계 (외래 키 매핑 및 unique = true로 1:1 보장)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mealId", nullable = false, unique = true)
    private Meal meal;

    @Column(name = "image_url")
    private String imageUrl;

    protected void setMeal(Meal meal) {
        this.meal = meal;
    }

    public void update(String newUrl) {
        this.imageUrl = newUrl;
    }

}
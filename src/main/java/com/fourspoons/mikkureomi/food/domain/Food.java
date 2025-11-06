package com.fourspoons.mikkureomi.food.domain;

import com.fourspoons.mikkureomi.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "food")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Food extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "food_cd")
    private String foodCd;   // 음식 코드

    @Column(name = "food_nm")
    private String foodNm;   // 음식명

    @Column(precision = 10, scale = 2)
    private BigDecimal enerc;  // 에너지 (kcal, 100g 기준)

    @Column(precision = 10, scale = 2)
    private BigDecimal prot;   // 단백질 (g)

    @Column(precision = 10, scale = 2)
    private BigDecimal fatce;  // 지방 (g)

    @Column(precision = 10, scale = 2)
    private BigDecimal chocdf; // 탄수화물 (g)

    @Column(precision = 10, scale = 2)
    private BigDecimal sugar;  // 당류 (g)

    @Column(precision = 10, scale = 2)
    private BigDecimal fibtg;  // 식이섬유 (g)

    @Column(precision = 10, scale = 2)
    private BigDecimal nat;    // 나트륨 (mg)

    @Column(precision = 10, scale = 2)
    private BigDecimal foodSize; // 1회 제공량 (단위 제거된 숫자)
}

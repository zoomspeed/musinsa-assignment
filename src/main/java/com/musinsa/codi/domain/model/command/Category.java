package com.musinsa.codi.domain.model.command;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;  // 기존 enum 값과 매핑될 코드 (TOP, OUTER, ...)

    @Column(nullable = false)
    private String name;  // 표시될 이름 (상의, 아우터, ...)

    @Column(nullable = false)
    private int displayOrder;  // 표시 순서

    private String description;  // 카테고리 설명

    @Builder
    public Category(String code, String name, int displayOrder, String description) {
        this.code = code;
        this.name = name;
        this.displayOrder = displayOrder;
        this.description = description;
    }

    public void update(String name, int displayOrder, String description) {
        this.name = name;
        this.displayOrder = displayOrder;
        this.description = description;
    }
} 
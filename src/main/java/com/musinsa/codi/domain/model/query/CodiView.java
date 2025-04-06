package com.musinsa.codi.domain.model.query;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "codi_views")
@Getter
@NoArgsConstructor
public class CodiView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outer_id")
    private ProductView outer;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "top_id")
    private ProductView top;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bottom_id")
    private ProductView bottom;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shoes_id")
    private ProductView shoes;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bag_id")
    private ProductView bag;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accessory_id")
    private ProductView accessory;

    @Column(nullable = false)
    private int totalPrice;

    @Builder
    public CodiView(Long id, String name, ProductView outer, ProductView top, ProductView bottom,
                   ProductView shoes, ProductView bag, ProductView accessory, int totalPrice) {
        this.id = id;
        this.name = name;
        this.outer = outer;
        this.top = top;
        this.bottom = bottom;
        this.shoes = shoes;
        this.bag = bag;
        this.accessory = accessory;
        this.totalPrice = totalPrice;
    }
} 
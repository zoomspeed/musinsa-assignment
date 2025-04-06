package com.musinsa.codi.common.dto.query;

import com.musinsa.codi.domain.model.query.CodiView;
import lombok.Getter;

@Getter
public class CodiResponse {
    private Long id;
    private String name;
    private ProductResponse outer;
    private ProductResponse top;
    private ProductResponse bottom;
    private ProductResponse shoes;
    private ProductResponse bag;
    private ProductResponse accessory;
    private int totalPrice;

    public static CodiResponse from(CodiView codiView) {
        CodiResponse response = new CodiResponse();
        response.id = codiView.getId();
        response.name = codiView.getName();
        response.outer = ProductResponse.from(codiView.getOuter());
        response.top = ProductResponse.from(codiView.getTop());
        response.bottom = ProductResponse.from(codiView.getBottom());
        response.shoes = ProductResponse.from(codiView.getShoes());
        response.bag = ProductResponse.from(codiView.getBag());
        response.accessory = ProductResponse.from(codiView.getAccessory());
        response.totalPrice = codiView.getTotalPrice();
        return response;
    }
} 
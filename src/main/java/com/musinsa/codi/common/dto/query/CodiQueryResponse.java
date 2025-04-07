package com.musinsa.codi.common.dto.query;

import com.musinsa.codi.domain.model.query.CodiView;
import lombok.Getter;

@Getter
public class CodiQueryResponse {
    private Long id;
    private String name;
    private ProductQueryResponse outer;
    private ProductQueryResponse top;
    private ProductQueryResponse bottom;
    private ProductQueryResponse shoes;
    private ProductQueryResponse bag;
    private ProductQueryResponse accessory;
    private int totalPrice;

    public static CodiQueryResponse from(CodiView codiView) {
        CodiQueryResponse response = new CodiQueryResponse();
        response.id = codiView.getId();
        response.name = codiView.getName();
        response.outer = ProductQueryResponse.from(codiView.getOuter());
        response.top = ProductQueryResponse.from(codiView.getTop());
        response.bottom = ProductQueryResponse.from(codiView.getBottom());
        response.shoes = ProductQueryResponse.from(codiView.getShoes());
        response.bag = ProductQueryResponse.from(codiView.getBag());
        response.accessory = ProductQueryResponse.from(codiView.getAccessory());
        response.totalPrice = codiView.getTotalPrice();
        return response;
    }
} 
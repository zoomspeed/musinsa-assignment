package com.musinsa.codi.domain.service.query.util;

import com.musinsa.codi.domain.model.query.ProductView;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class PriceCalculator {
    private PriceCalculator() {
        throw new IllegalStateException("Utility class");
    }

    public static Optional<ProductView> findLowestPriceProduct(Collection<ProductView> products) {
        return products.stream()
                .min(Comparator.comparingInt(ProductView::getPrice));
    }

    public static Optional<ProductView> findHighestPriceProduct(Collection<ProductView> products) {
        return products.stream()
                .max(Comparator.comparingInt(ProductView::getPrice));
    }

    public static int calculateTotalPrice(Collection<ProductView> products) {
        return products.stream()
                .mapToInt(ProductView::getPrice)
                .sum();
    }

    public static List<ProductView> filterByPriceRange(Collection<ProductView> products, int minPrice, int maxPrice) {
        return products.stream()
                .filter(product -> product.getPrice() >= minPrice && product.getPrice() <= maxPrice)
                .toList();
    }
} 
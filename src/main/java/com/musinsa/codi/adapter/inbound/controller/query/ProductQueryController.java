package com.musinsa.codi.adapter.inbound.controller.query;

import com.musinsa.codi.domain.model.Category;
import com.musinsa.codi.domain.model.query.ProductView;
import com.musinsa.codi.domain.port.query.ProductQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductQueryController {
    private final ProductQueryPort productQueryPort;

    @GetMapping
    public ResponseEntity<List<ProductView>> getAllProducts() {
        return ResponseEntity.ok(productQueryPort.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductView> getProductById(@PathVariable Long id) {
        return productQueryPort.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductView>> getProductsByCategory(@PathVariable Category category) {
        return ResponseEntity.ok(productQueryPort.findByCategory(category));
    }

    @GetMapping("/category/{category}/price-range")
    public ResponseEntity<List<ProductView>> getProductsByPriceRange(
            @PathVariable Category category,
            @RequestParam int minPrice,
            @RequestParam int maxPrice) {
        return ResponseEntity.ok(productQueryPort.findByPriceRange(category, minPrice, maxPrice));
    }

    @GetMapping("/brand/{brandId}")
    public ResponseEntity<List<ProductView>> getProductsByBrandId(@PathVariable Long brandId) {
        return ResponseEntity.ok(productQueryPort.findByBrandId(brandId));
    }
} 
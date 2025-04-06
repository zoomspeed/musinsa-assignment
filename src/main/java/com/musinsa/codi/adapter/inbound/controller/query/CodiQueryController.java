package com.musinsa.codi.adapter.inbound.controller.query;

import com.musinsa.codi.common.dto.query.CodiQueryResponse;
import com.musinsa.codi.domain.service.query.CodiQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/codi")
@RequiredArgsConstructor
public class CodiQueryController {
    private final CodiQueryService codiQueryService;

    @GetMapping
    public ResponseEntity<List<CodiQueryResponse>> getAllCodis() {
        List<CodiQueryResponse> responses = codiQueryService.getAllCodis().stream()
                .map(CodiQueryResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CodiQueryResponse> getCodiById(@PathVariable Long id) {
        CodiQueryResponse response = CodiQueryResponse.from(codiQueryService.getCodiById(id));
        return ResponseEntity.ok(response);
    }
} 
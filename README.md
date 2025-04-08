# MUSINSA Backend Assignment

무신사 백엔드 과제 - 카테고리별 상품 가격 비교 및 관리 API

## 구현 범위

### 1. 카테고리별 최저가 조회 API
- 엔드포인트: `GET /api/v1/categories/lowest-price`
- 각 카테고리별 최저가 브랜드와 가격, 총액을 조회
- 성공 응답 예시:
```json
{
  "categories": [
    {
      "category": "상의",
      "brand": "C",
      "price": 10000
    },
    // ... 다른 카테고리들
  ],
  "totalPrice": 34100
}
```

### 2. 단일 브랜드 최저가 조회 API
- 엔드포인트: `GET /api/v1/brands/lowest-price`
- 모든 카테고리 상품을 단일 브랜드에서 구매할 때 최저가 브랜드와 가격 정보 조회
- 성공 응답 예시:
```json
{
  "brandName": "D",
  "categories": [
    {
      "categoryName": "상의",
      "price": 10100
    },
    // ... 다른 카테고리들
  ],
  "totalPrice": 36100
}
```
- 실패 응답 예시:
```json
{
  "status": 404,
  "message": "브랜드를 찾을 수 없습니다: {{brandName}}"
}
```

### 3. 카테고리별 최저/최고가 조회 API
- 엔드포인트: `GET /api/v1/categories/price-range-info`
- 요청 파라미터: `categoryCode` (String)
- 특정 카테고리의 최저가와 최고가 브랜드 및 가격 정보 조회
- 성공 응답 예시:
```json
{
  "category": "상의",
  "lowestPrice": [
    {
      "brandName": "C",
      "price": 10000
    }
  ],
  "highestPrice": [
    {
      "brandName": "I",
      "price": 11400
    }
  ]
}
```
- 실패 응답 예시:
```json
{
  "status": 404,
  "message": "카테고리를 찾을 수 없습니다: {{categoryName}}"
}
```

### 4. 브랜드 및 상품 관리 API

#### 브랜드 관리 API
- 브랜드 목록 조회
  - 엔드포인트: `GET /api/v1/brands`
  - 성공 응답 예시:
  ```json
  [
    {
      "id": 1,
      "name": "A"
    },
    {
      "id": 2,
      "name": "B"
    },
    {
      "id": 3,
      "name": "C"
    },
    ...
    {
      "id": 9,
      "name": "I"
    }
  ]
  ```

- 브랜드 단일 조회
  - 엔드포인트: `GET /api/v1/brands/{brandName}`
  - 성공 응답 예시:
  ```json
  {
    "id": 10,
    "name": "new-brand",
    "products": [
      {
        "id": 73,
        "productId": 73,
        "categoryCode": "ACCESSORY",
        "categoryName": "액세서리",
        "price": 10000,
        "brandId": 10,
        "brandName": "new-brand"
      }
      ...
    ]
  }
  ```
  - 실패 응답 예시:
  ```json
  {
    "status": 404,
    "message": "브랜드를 찾을 수 없습니다: {{brandName}}"
  }
  ```

- 브랜드 생성
  - 엔드포인트: `POST /api/v1/brands`
  - 요청 예시:
  ```json
  {
    "name": "new-brand"
  }
  ```
  - 성공 응답 예시:
  ```json
  {
    "success": true,
    "message": "브랜드 new-brand가 성공적으로 생성되었습니다.",
    "brandId": 10
  }
  ```
  - 실패 응답 예시:
  ```json
  {
    "status": 409,
    "message": "이미 존재하는 브랜드입니다: 이미 존재하는 브랜드 이름입니다: {{brandName}}"
  }
  ```

- 브랜드 수정
  - 엔드포인트: `PUT /api/v1/brands/{brandName}`
  - 요청 예시:
  ```json
  {
    "name" : "new-brand-change"
  }
  ```
  - 성공 응답 예시:
  ```json
  {
    "success": true,
    "message": "브랜드명이 {{이전 브랜드명}}에서 {{변경된 브랜드명}}로 성공적으로 변경되었습니다.",
    "brandId": 10
  }
  ```
  - 실패 응답 예시:
  ```json
  {
    "status": 404,
    "message": "브랜드를 찾을 수 없습니다: 존재하지 않는 브랜드입니다: {{brandName}}"
  }
  ```

- 브랜드 삭제
  - 엔드포인트: `DELETE /api/v1/brands/{brandName}`
  - 성공 응답: 204 No Content
  - 실패 응답 예시:
  - 브랜드가 존재하지 않을경우 실패 응답
  ```json
  {
    "status": 404,
    "message": "브랜드를 찾을 수 없습니다: 존재하지 않는 브랜드입니다: {{brandName}}"
  }
  ```
  - 브랜드 내부에 상품이 존재하면 삭제 실패처리
  ```json
  {
    "status": 409,
    "message": "해당 브랜드에 이미 상품이 존재하여 브랜드를 삭제할 수 없습니다. 이미 브랜드 내부에 상품이 존재하여 삭제할 수 없습니다: {{brandName}}"
  }
  ```

#### 상품 관리 API
- 상품 목록 조회
  - 엔드포인트: `GET /api/v1/products`
  - 선택적 쿼리 파라미터:
    - `brandName`: 브랜드명으로 필터링
    - `categoryCode`: 카테고리로 필터링
  - 요청 예시:
  ```json
  {
    "name": "product-3",
    "categoryCode": "ACCESSORY",
    "price": 10000
  }
  ```
  - 성공 응답 예시:
  ```json
  {
    "success": true,
    "message": "브랜드 {{brandName}}에 상품 {{productName}}가 성공적으로 추가되었습니다.",
    "productId": 75,
    "brandId": null
  }
  ```
  - 실패 응답 예시:
  - 브랜드가 존재하지 않을 때:
  ```json
  {
    "status": 404,
    "message": "브랜드를 찾을 수 없습니다: {{brandName}}"
  }
  ```

- 상품 단일 조회
  - 엔드포인트: `GET /api/v1/products/{productId}`
  - 성공 응답 예시:
  ```json
    {
      "id": 1,
      "productId": 1,
      "categoryCode": "TOP",
      "categoryName": "상의",
      "price": 11200,
      "brandId": 1,
      "brandName": "A"
    }
    ```
    - 실패 응답 예시:
    ```json
    {
      "status": 404,
      "message": "상품을 찾을 수 없습니다: {{productName}}"
    }
    ```

- 상품 생성
  - 엔드포인트: `POST /v1/brands/{brandName}/products`
  - 요청 예시:
  ```json
  {
    "name": "기본 티셔츠",
    "categoryCode": "TOP",
    "price": 10000
  }
  ```
  - 성공 응답 예시:
  ```json
  {
    "success": true,
    "message": "브랜드 new-brand에 상품 product-3가 성공적으로 추가되었습니다.",
    "productId": 73,
    "brandId": null
  }
  ```
  - 실패 응답 예시:
  ```json
  {
    "status": 404,
    "message": "브랜드를 찾을 수 없습니다: {{brandName}}"
  }
  ```

- 상품 수정
  - 엔드포인트: `PUT /api/v1/brands/{brandName}/products/{productId}`
  - 요청 예시:
  ```json
  {
    "name": "프리미엄 티셔츠",
    "price": 15000
  }
  ```
  - 성공 응답 예시:
  ```json
  {
    "success": true,
    "message": "브랜드 new-brand의 상품 product-3가 성공적으로 업데이트되었습니다.",
    "productId": 74,
    "brandId": null
  }
  ```
  - 실패 응답 예시 1:
  - 브랜드가 존재하지 않을 때
  ```json
  {
    "status": 404,
    "message": "브랜드를 찾을 수 없습니다: {{brandName}}"
  }
  ```
  - 실패 응답 예시 2:
  - 상품번호가 존재하지 않을 때
  ```json
  {
    "status": 404,
    "message": "상품을 찾을 수 없습니다: {{productId}}"
  }  
  ```

- 상품 삭제
  - 엔드포인트: `DELETE /api/v1/brands/{brandName}/products/{productId}`
  - 성공 응답: 200 OK
  ```json
  {
    "success": true,
    "message": "브랜드 new-brand의 상품(ID: {{productId}})이 성공적으로 삭제되었습니다. ",
    "productId": 73,
    "brandId": null
  }
  ```
  - 실패 응답 예시:
  ```json
  {
    "status": 404,
    "message": "브랜드를 찾을 수 없습니다: {{brandName}}"
  }
  ```

## 프로젝트 구조

### 도메인 주도 설계(DDD) 적용
- 도메인 레이어: 핵심 비즈니스 로직 구현
- 애플리케이션 레이어: 유스케이스 정의
- 어댑터 레이어: 외부 시스템과의 통신 처리
- 인프라스트럭처 레이어: 기술적 구현 제공

### 주요 컴포넌트
- `PriceCalculator`: 가격 계산 관련 유틸리티 클래스
- `CategoryQueryService`: 카테고리 조회 관련 비즈니스 로직
- `BrandQueryService`: 브랜드 조회 관련 비즈니스 로직
- `ProductCommandService`: 상품 관리 관련 비즈니스 로직

## 기술 스택
- Java 17
- Spring Boot 3.2.3
- Spring Data JPA
- H2 Database
- Lombok
- Gradle

## 빌드 및 실행 방법

### 요구사항
- JDK 17 이상
- Gradle 8.x

### 빌드
```bash
./gradlew clean build
```

### 실행
```bash
./gradlew bootRun
```

### API 테스트

#### 구현 1) 카테고리별 최저가격 브랜드와 총액 조회
```bash
# 카테고리별 최저가 조회
curl -X GET http://localhost:8080/api/v1/categories/lowest-price
```

#### 구현 2) 단일 브랜드 전체 카테고리 최저가격 조회
```bash
# 단일 브랜드 최저가 조회
curl -X GET http://localhost:8080/api/v1/brands/lowest-price
```

#### 구현 3) 카테고리별 최저/최고가격 브랜드 조회
```bash
# 카테고리별 최저/최고가 조회
curl -X GET "http://localhost:8080/api/v1/categories/price-range-info?categoryCode=TOP"
```

#### 구현 4) 브랜드 및 상품 관리
```bash
# 브랜드 관리 API
## 브랜드 목록 조회
curl -X GET http://localhost:8080/api/v1/brands

## 브랜드 단일 조회
curl -X GET http://localhost:8080/api/v1/brands/NEW_BRAND

## 브랜드 생성
curl -X POST http://localhost:8080/api/v1/brands \
  -H "Content-Type: application/json" \
  -d '{"name": "NEW_BRAND", "description": "새로운 브랜드"}'

## 브랜드 수정
curl -X PUT http://localhost:8080/api/v1/brands/NEW_BRAND \
  -H "Content-Type: application/json" \
  -d '{"description": "수정된 브랜드 설명"}'

## 브랜드 삭제
curl -X DELETE http://localhost:8080/api/v1/brands/NEW_BRAND

# 상품 관리 API
## 상품 목록 조회
curl -X GET "http://localhost:8080/api/v1/products?brandName=NEW_BRAND&categoryCode=TOP"

## 상품 단일 조회
curl -X GET http://localhost:8080/api/v1/products/1

## 상품 생성
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{"name": "기본 티셔츠", "brandName": "NEW_BRAND", "categoryCode": "TOP", "price": 10000}'

## 상품 수정
curl -X PUT http://localhost:8080/api/v1/products/1 \
  -H "Content-Type: application/json" \
  -d '{"name": "프리미엄 티셔츠", "price": 15000}'

## 상품 삭제
curl -X DELETE http://localhost:8080/api/v1/products/1
```

## 에러 처리
- 모든 API는 실패 시 적절한 에러 메시지와 HTTP 상태 코드 반환
- `BusinessException`을 통한 일관된 예외 처리
- 글로벌 예외 핸들러를 통한 통합된 에러 응답 포맷

## 데이터베이스
- H2 인메모리 데이터베이스 사용
- 서버 시작 시 초기 데이터 자동 로드
- 접속 정보:
  - URL: `jdbc:h2:mem:musinsadb`
  - Username: `sa`
  - Password: 없음

## 향후 개선 사항
- [ ] Unit Test 및 Integration Test 작성
- [ ] API 문서화 (Swagger/Spring Rest Docs)
- [ ] 프론트엔드 구현
- [ ] 성능 최적화
- [ ] 로깅 및 모니터링 추가
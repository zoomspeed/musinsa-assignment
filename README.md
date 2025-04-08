# 무신사 백엔드 과제

## 프로젝트 소개
의류 브랜드와 상품을 관리하고, 카테고리별 최저가를 조회할 수 있는 REST API 서비스입니다.

### 핵심 기능
1. 카테고리별 최저가 브랜드와 가격 조회
2. 단일 브랜드로 모든 카테고리 상품 구매시 최저가 조회
3. 특정 카테고리의 최저가/최고가 브랜드와 가격 조회
4. 브랜드와 상품 관리 (CRUD)

## 프로젝트 구조

### 도메인 주도 설계 (DDD) 적용
프로젝트는 DDD 원칙에 따라 다음과 같이 구성되어 있습니다:

```
src/main/java/com/musinsa/codi
├── adapter
│   ├── inbound
│   │   └── controller        # REST API 컨트롤러
│   └── outbound
│       ├── persistence       # 영속성 어댑터
│       └── event            # 이벤트 처리
├── application
│   └── usecase              # 유스케이스 인터페이스
├── common
│   ├── annotation          # 커스텀 어노테이션
│   ├── dto                 # 요청/응답 DTO
│   ├── exception          # 예외 처리
│   └── util               # 유틸리티
└── domain
    ├── event              # 도메인 이벤트
    ├── model              # 도메인 모델
    │   ├── command       # 커맨드 모델
    │   └── query        # 쿼리 모델
    ├── port              # 포트 인터페이스
    └── service           # 도메인 서비스
```

### CQRS 패턴 적용
- Command와 Query를 분리하여 각각의 책임을 명확히 하고 성능을 최적화했습니다.
- Command: 브랜드와 상품의 생성, 수정, 삭제를 담당
- Query: 카테고리별 가격 조회와 브랜드/상품 조회를 담당

### 헥사고날 아키텍처 적용
- 도메인 로직을 외부 인프라스트럭처로부터 격리
- Port와 Adapter를 통한 느슨한 결합
- 테스트 용이성 향상

### 이벤트 기반 아키텍처
- 도메인 이벤트를 통한 Command와 Query 모델 동기화
- `@PublishBrandEvent`, `@PublishProductEvent` 어노테이션을 통한 선언적 이벤트 발행

## 기술 스택
- Java 17
- Spring Boot 3.2.3
- Spring Data JPA
- H2 Database
- Lombok
- JUnit 5

## 빌드 및 실행 방법

### 요구사항
- Java 17 이상
- Maven 3.6 이상

### 빌드
```bash
./mvnw clean package
```

### 실행
```bash
./mvnw spring-boot:run
```

### 데이터베이스
- H2 인메모리 데이터베이스 사용
- 서버 시작 시 자동으로 스키마 생성 및 초기 데이터 로드
- H2 콘솔: http://localhost:8080/h2-console
  - JDBC URL: jdbc:h2:mem:musinsadb
  - Username: sa
  - Password: 없음

## API 문서

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
    "brandId": 10
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
    "brandId": 10
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

## 에러 처리
- 모든 API는 일관된 에러 응답 형식을 사용합니다
- 비즈니스 예외는 `BusinessException`을 통해 처리
- 각 에러에 대한 고유한 에러 코드와 메시지 제공

## 데이터베이스
- H2 인메모리 데이터베이스 사용
- 서버 시작 시 초기 데이터 자동 로드
- 접속 정보:
  - URL: `jdbc:h2:mem:musinsadb`
  - Username: `sa`
  - Password: 없음

## 향후 개선 사항
1. 단위 테스트 및 통합 테스트 보강
2. API 문서화 (Swagger/Spring Rest Docs)
3. 프론트엔드 구현
4. 성능 최적화
   - 캐싱 적용
   - 쿼리 최적화
5. 모니터링 및 로깅 강화
# 무신사 백엔드 과제

## 프로젝트 소개

개발하면서 가장 중점을 둔 부분은 다음과 같습니다:
- 실제 서비스에서 발생할 수 있는 성능 이슈 해결
- 확장 가능하고 유지보수하기 좋은 아키텍처 설계
- 명확한 도메인 개념 설계와 구현

### 핵심 기능
구현된 주요 기능들을 소개해드리겠습니다:

1. 카테고리별로 어떤 브랜드의 상품이 가장 저렴한지 조회하실 수 있습니다
2. 한 브랜드에서 모든 종류의 옷을 구매할 때 최저가 브랜드를 찾아드립니다
3. 특정 카테고리에서 최저가/최고가 브랜드와 가격을 확인하실 수 있습니다
4. 브랜드와 상품을 편리하게 관리하실 수 있습니다

## 도메인 설계

프로젝트를 시작하면서 가장 먼저 고민했던 부분은 도메인 설계였습니다. 실제 의류 쇼핑몰의 비즈니스를 최대한 반영하고자 했습니다.

### 핵심 도메인
1. **브랜드 (Brand)**
   - 브랜드는 여러 상품을 보유할 수 있는 독립적인 엔티티로 설계했습니다
   - 브랜드명은 중복되면 안 되기에 유일성을 보장했습니다
   - 상품과는 1:N 관계를 가지도록 했습니다

2. **상품 (Product)**
   - 상품은 반드시 하나의 브랜드에 속하도록 설계했습니다
   - 카테고리 정보와 가격 정보를 필수로 가지도록 했습니다
   - 브랜드, 카테고리와 각각 N:1 관계를 맺도록 했습니다

3. **카테고리 (Category)**
   - 상품 분류를 위한 독립적인 엔티티입니다
   - 카테고리 코드의 유일성을 보장했습니다
   - 상품과 1:N 관계를 가집니다

### 데이터베이스 설계

성능과 확장성을 고려하여 Command와 Query 모델을 분리했습니다. 각각의 특징을 설명드리겠습니다.

#### Command 모델 (정규화된 스키마)
정규화된 스키마를 통해 데이터 정합성을 보장했습니다:

```sql
-- 브랜드 테이블
CREATE TABLE brands (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- 카테고리 테이블
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL
);

-- 상품 테이블
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price INT NOT NULL,
    brand_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    FOREIGN KEY (brand_id) REFERENCES brands(id),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);
```

#### Query 모델 (역정규화된 스키마)
조회 성능 최적화를 위해 데이터를 역정규화했습니다:

```sql
-- 브랜드 뷰 테이블
CREATE TABLE brand_view (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- 상품 뷰 테이블 (조회 최적화)
CREATE TABLE product_view (
    id BIGINT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    price INT NOT NULL,
    brand_id BIGINT NOT NULL,
    brand_name VARCHAR(255) NOT NULL,
    category_id BIGINT NOT NULL,
    category_code VARCHAR(50) NOT NULL,
    category_name VARCHAR(255) NOT NULL
);
```

### CQRS 패턴 구현

CQRS 패턴을 적용하면서 많은 고민이 있었습니다. 그 과정에서 얻은 인사이트를 공유드리고자 합니다.

#### Command와 Query를 분리한 이유
1. **성능 최적화**
   - 복잡한 조회 쿼리의 성능을 개선하기 위해 별도의 역정규화된 스키마를 사용했습니다
   - 조인 연산을 최소화하여 빠른 데이터 조회가 가능해졌습니다

2. **관심사 분리**
   - 상태를 변경하는 로직과 조회하는 로직을 명확하게 분리했습니다
   - 각 모델을 독립적으로 최적화할 수 있게 되었습니다

3. **확장성 확보**
   - 조회 모델은 필요에 따라 독립적으로 확장할 수 있습니다
   - 캐싱 전략도 조회 모델에 맞춰 유연하게 적용할 수 있게 되었습니다

#### 데이터 동기화는 이렇게 구현했습니다

1. **도메인 이벤트 기반 동기화**
```java
@PublishBrandEvent(eventType = BrandEventType.UPDATED)
@PublishProductEvent(eventType = ProductEventType.CREATED)
public Product addProduct(String brandName, ProductCommandRequest request) {
    // 상품 생성 로직
}
```

2. **이벤트 리스너를 통한 뷰 테이블 갱신**
```java
@EventListener
public void onBrandEvent(BrandEvent event) {
    switch (event.getEventType()) {
        case CREATED -> handleCreateEvent(event.getBrand());
        case UPDATED -> handleUpdateEvent(event.getBrand());
        case DELETED -> handleDeleteEvent(event.getBrand());
    }
}
```

3. **트랜잭션 관리**
   - Command 모델에서는 강한 일관성을 보장하도록 했습니다
   - Query 모델은 최종 일관성을 허용하여 성능을 확보했습니다

### 헥사고날 아키텍처 적용 내용

계층 구조를 설계하면서 다음과 같은 점들을 고려했습니다:

1. **도메인 계층** (Domain Layer)
   - 핵심 비즈니스 로직은 외부 의존성 없이 순수하게 구현했습니다
   ```java
   public class Brand {
       private Long id;
       private String name;
       private List<Product> products;
       
       public void addProduct(Product product) {
           validateProductId(product);
           product.setBrand(this);
           products.add(product);
       }
   }
   ```

2. **응용 계층** (Application Layer)
   - 유스케이스를 명확하게 정의하고 트랜잭션을 관리합니다
   ```java
   public interface BrandCommandUseCase {
       Brand createBrand(BrandCommandRequest request);
       Brand updateBrand(String brandName, BrandCommandRequest request);
       Brand deleteBrand(String brandName);
   }
   ```

3. **어댑터 계층** (Adapter Layer)
   - REST API와 데이터베이스 연동을 담당하는 부분입니다
   ```java
   @RestController
   @RequestMapping("/api/v1/brands")
   public class BrandCommandController {
       private final BrandCommandUseCase brandCommandUseCase;
       // API 엔드포인트 구현
   }
   ```

#### 포트 인터페이스
1. **인바운드 포트**
   - 외부에서 애플리케이션을 사용하기 위한 인터페이스
   - UseCase 인터페이스로 구현

2. **아웃바운드 포트**
   - 애플리케이션이 외부 시스템을 사용하기 위한 인터페이스
   - Repository, EventPublisher 등으로 구현

### 이벤트 기반 아키텍처

#### 도메인 이벤트
1. **이벤트 정의**
   ```java
   public class BrandEvent implements DomainEvent {
       private final Brand brand;
       private final BrandEventType eventType;
   }
   ```

2. **이벤트 발행**
   - AOP를 활용한 선언적 이벤트 발행
   ```java
   @PublishBrandEvent(eventType = BrandEventType.CREATED)
   public Brand createBrand(BrandCommandRequest request) {
       // 브랜드 생성 로직
   }
   ```

3. **이벤트 구독**
   - 스프링의 이벤트 리스너 활용
   - Query 모델 동기화 처리

### 성능 최적화

1. **조회 성능 최적화**
   - 역정규화된 뷰 테이블 사용
   - 복잡한 조인 연산 제거

2. **쓰기 성능 최적화**
   - 비동기 이벤트 처리
   - 배치 처리 가능성 고려

3. **확장성 고려사항**
   - 읽기/쓰기 분리로 독립적인 스케일링 가능
   - 캐시 적용 용이

## 아키텍처 설계 의도와 고민했던 점들

안녕하세요. 이 프로젝트의 아키텍처를 설계하면서 고민했던 점들을 공유드리고자 합니다.

### CQRS 패턴 도입 배경

처음에는 전통적인 3계층 구조로 시작했습니다. 개발을 진행하면서 다음과 같은 문제들을 경험했습니다:

1. **복잡한 조회 로직으로 인한 성능 이슈**
   ```sql
   -- 카테고리별 최저가 조회시 발생했던 문제점
   SELECT p.*, b.name as brand_name, c.name as category_name
   FROM products p
   JOIN brands b ON p.brand_id = b.id
   JOIN categories c ON p.category_id = c.id
   WHERE p.price = (
     SELECT MIN(price) 
     FROM products sub_p
     WHERE sub_p.category_id = p.category_id
   )
   ```
   
   이런 방식의 쿼리는 다음과 같은 문제점들이 있었습니다:
   - 상품 데이터가 늘어날수록 서브쿼리 실행 비용이 기하급수적으로 증가
   - 카테고리별로 인덱스를 타지 못하고 테이블 풀 스캔이 발생
   - 동시 요청이 많아질 때 DB 부하가 크게 증가

2. **확장성 관련 경험했던 이슈들**
   - 상품 가격 변경시 캐시 처리의 어려움
     ```java
     @Transactional
     public void updatePrice(Long productId, int newPrice) {
         productRepository.updatePrice(productId, newPrice);
         // 가격 변경시 연관된 카테고리의 최저가 캐시도 함께 갱신해야 하는 이슈
         categoryPriceCache.invalidateAll();
     }
     ```
   - 할인 이벤트 같이 트래픽이 많이 몰리는 기간에는 다음과 같은 문제들이 발생
     - 가격 변경과 조회가 동시에 많이 발생하면서 성능 저하
     - 동시성 처리를 위한 락으로 인한 응답 지연

이러한 문제들을 해결하기 위해 CQRS 패턴을 도입했고, 다음과 같은 개선을 할 수 있었습니다:

```
[Command 모델 (가격 변경, 상품 추가 등)]
- 정규화된 스키마로 데이터 정합성 관리
- 트랜잭션 격리 수준 조정으로 동시성 제어
- 이벤트 기반 비동기 처리로 성능 개선

[Query 모델 (가격 조회, 카테고리별 조회 등)]
- 조회에 최적화된 별도의 뷰 테이블 구성
- 복잡한 조인이나 서브쿼리 없이 조회 가능
- Redis 캐시를 통한 응답 속도 개선
```

특히 카테고리별 최저가 조회의 경우:
- 복잡한 조인과 서브쿼리가 필요 없어져 쿼리 성능이 크게 개선
- 캐시 적용이 용이해져 응답 속도 향상
- 이벤트 기간에도 안정적인 성능 유지 가능

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

## 기술 스택
- Java 17
- Spring Boot 3.2.3
- Spring Data JPA
- H2 Database
- Lombok
- JUnit 5

## 빌드 및 실행 방법

### 실행 환경 준비사항
- Java 17 이상이 설치되어 있어야 합니다
- Gradle 8.5 이상이 필요합니다

### 빌드하기
다음 명령어로 프로젝트를 빌드하실 수 있습니다:
```bash
./gradlew clean build
```

### 실행하기
애플리케이션을 실행하려면 다음 명령어를 입력해주세요:
```bash
./gradlew bootRun
```

### 데이터베이스 접속 정보
개발의 편의성을 위해 H2 인메모리 데이터베이스를 사용했습니다:
- H2 콘솔: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:musinsadb`
- 사용자명: `sa`
- 비밀번호: 없음

## 향후 개선 계획

더 나은 서비스를 위해 다음과 같은 개선을 계획하고 있습니다:

1. **테스트 강화**
   - 단위 테스트 커버리지 향상
   - 통합 테스트 시나리오 추가
   - 성능 테스트 도입

2. **API 문서화 개선**
   - Swagger UI 적용
   - Spring Rest Docs를 통한 정확한 문서화

3. **프론트엔드 개발**
   - 사용자 친화적인 웹 인터페이스 구현
   - 반응형 디자인 적용

4. **성능 최적화**
   - 캐시 적용 범위 확대
   - 쿼리 최적화
   - 대용량 트래픽 대비

5. **운영 환경 개선**
   - 모니터링 시스템 구축
   - 로깅 체계 개선
   - 장애 대응 전략 수립

위 개선사항들은 실제 서비스 운영 경험을 바탕으로 지속적으로 발전시켜 나갈 예정입니다.

## API 문서

구현된 API들을 자세히 설명드리겠습니다.

### 1. 카테고리별 최저가 조회 API
각 카테고리별로 가장 저렴한 브랜드와 가격을 확인하실 수 있습니다.
- 엔드포인트: `GET /api/v1/categories/lowest-price`
- 응답 예시:
```json
{
  "categories": [
    {
      "category": "상의",
      "brand": "C",
      "price": 10000
    }
    // ... 다른 카테고리들
  ],
  "totalPrice": 34100
}
```

### 2. 단일 브랜드 최저가 조회 API
한 브랜드에서 모든 카테고리 상품을 구매할 때 어떤 브랜드가 가장 저렴한지 알려드립니다.
- 엔드포인트: `GET /api/v1/brands/lowest-price`
- 응답 예시:
```json
{
  "brandName": "D",
  "categories": [
    {
      "categoryName": "상의",
      "price": 10100
    }
    // ... 다른 카테고리들
  ],
  "totalPrice": 36100
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

## 향후 개선 계획

더 나은 서비스를 위해 다음과 같은 개선을 계획하면 더 좋을것 같습니다:

1. **테스트 강화**
   - 단위 테스트 커버리지 향상
   - 통합 테스트 시나리오 추가
   - 성능 테스트 도입

2. **API 문서화 개선**
   - Swagger UI 적용
   - Spring Rest Docs를 통한 정확한 문서화

3. **프론트엔드 개발**
   - 사용자 친화적인 웹 인터페이스 구현
   - 반응형 디자인 적용

4. **성능 최적화**
   - 캐시 적용 범위 확대
   - 쿼리 최적화
   - 대용량 트래픽 대비

5. **운영 환경 개선**
   - 모니터링 시스템 구축
   - 로깅 체계 개선
   - 장애 대응 전략 수립

위 개선사항들은 실제 서비스 운영 경험을 바탕으로 지속적으로 발전시켜 나갈 예정입니다.

## API 문서

구현된 API들을 자세히 설명드리겠습니다.

### 1. 카테고리별 최저가 조회 API
각 카테고리별로 가장 저렴한 브랜드와 가격을 확인하실 수 있습니다.
- 엔드포인트: `GET /api/v1/categories/lowest-price`
- 응답 예시:
```json
{
  "categories": [
    {
      "category": "상의",
      "brand": "C",
      "price": 10000
    }
    // ... 다른 카테고리들
  ],
  "totalPrice": 34100
}
```

### 2. 단일 브랜드 최저가 조회 API
한 브랜드에서 모든 카테고리 상품을 구매할 때 어떤 브랜드가 가장 저렴한지 알려드립니다.
- 엔드포인트: `GET /api/v1/brands/lowest-price`
- 응답 예시:
```json
{
  "brandName": "D",
  "categories": [
    {
      "categoryName": "상의",
      "price": 10100
    }
    // ... 다른 카테고리들
  ],
  "totalPrice": 36100
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

## 향후 개선 계획

더 나은 서비스를 위해 다음과 같은 개선을 계획하면 더 좋을것 같습니다:

1. **테스트 강화**
   - 단위 테스트 커버리지 향상
   - 통합 테스트 시나리오 추가
   - 성능 테스트 도입

2. **API 문서화 개선**
   - Swagger UI 적용
   - Spring Rest Docs를 통한 정확한 문서화

3. **프론트엔드 개발**
   - 사용자 친화적인 웹 인터페이스 구현
   - 반응형 디자인 적용

4. **성능 최적화**
   - 캐시 적용 범위 확대
   - 쿼리 최적화
   - 대용량 트래픽 대비

5. **운영 환경 개선**
   - 모니터링 시스템 구축
   - 로깅 체계 개선
   - 장애 대응 전략 수립

위 개선사항들은 실제 서비스 운영 경험을 바탕으로 지속적으로 발전시켜 나갈 예정입니다.

## API 문서

구현된 API들을 자세히 설명드리겠습니다.

### 1. 카테고리별 최저가 조회 API
각 카테고리별로 가장 저렴한 브랜드와 가격을 확인하실 수 있습니다.
- 엔드포인트: `GET /api/v1/categories/lowest-price`
- 응답 예시:
```json
{
  "categories": [
    {
      "category": "상의",
      "brand": "C",
      "price": 10000
    }
    // ... 다른 카테고리들
  ],
  "totalPrice": 34100
}
```

### 2. 단일 브랜드 최저가 조회 API
한 브랜드에서 모든 카테고리 상품을 구매할 때 어떤 브랜드가 가장 저렴한지 알려드립니다.
- 엔드포인트: `GET /api/v1/brands/lowest-price`
- 응답 예시:
```json
{
  "brandName": "D",
  "categories": [
    {
      "categoryName": "상의",
      "price": 10100
    }
    // ... 다른 카테고리들
  ],
  "totalPrice": 36100
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

## 향후 개선 계획

더 나은 서비스를 위해 다음과 같은 개선을 계획하면 더 좋을것 같습니다:

1. **테스트 강화**
   - 단위 테스트 커버리지 향상
   - 통합 테스트 시나리오 추가
   - 성능 테스트 도입

2. **API 문서화 개선**
   - Swagger UI 적용
   - Spring Rest Docs를 통한 정확한 문서화

3. **프론트엔드 개발**
   - 사용자 친화적인 웹 인터페이스 구현
   - 반응형 디자인 적용

4. **성능 최적화**
   - 캐시 적용 범위 확대
   - 쿼리 최적화
   - 대용량 트래픽 대비

5. **운영 환경 개선**
   - 모니터링 시스템 구축
   - 로깅 체계 개선
   - 장애 대응 전략 수립

위 개선사항들은 실제 서비스 운영 경험을 바탕으로 지속적으로 발전시켜 나갈 예정입니다.

## API 문서

구현된 API들을 자세히 설명드리겠습니다.

### 1. 카테고리별 최저가 조회 API
각 카테고리별로 가장 저렴한 브랜드와 가격을 확인하실 수 있습니다.
- 엔드포인트: `GET /api/v1/categories/lowest-price`
- 응답 예시:
```json
{
  "categories": [
    {
      "category": "상의",
      "brand": "C",
      "price": 10000
    }
    // ... 다른 카테고리들
  ],
  "totalPrice": 34100
}
```

### 2. 단일 브랜드 최저가 조회 API
한 브랜드에서 모든 카테고리 상품을 구매할 때 어떤 브랜드가 가장 저렴한지 알려드립니다.
- 엔드포인트: `GET /api/v1/brands/lowest-price`
- 응답 예시:
```json
{
  "brandName": "D",
  "categories": [
    {
      "categoryName": "상의",
      "price": 10100
    }
    // ... 다른 카테고리들
  ],
  "totalPrice": 36100
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

## 향후 개선 계획

더 나은 서비스를 위해 다음과 같은 개선을 계획하면 더 좋을것 같습니다:

1. **테스트 강화**
   - 단위 테스트 커버리지 향상
   - 통합 테스트 시나리오 추가
   - 성능 테스트 도입

2. **API 문서화 개선**
   - Swagger UI 적용
   - Spring Rest Docs를 통한 정확한 문서화

3. **프론트엔드 개발**
   - 사용자 친화적인 웹 인터페이스 구현
   - 반응형 디자인 적용

4. **성능 최적화**
   - 캐시 적용 범위 확대
   - 쿼리 최적화
   - 대용량 트래픽 대비

5. **운영 환경 개선**
   - 모니터링 시스템 구축
   - 로깅 체계 개선
   - 장애 대응 전략 수립

위 개선사항들은 실제 서비스 운영 경험을 바탕으로 지속적으로 발전시켜 나갈 예정입니다.

## API 문서

구현된 API들을 자세히 설명드리겠습니다.

### 1. 카테고리별 최저가 조회 API
각 카테고리별로 가장 저렴한 브랜드와 가격을 확인하실 수 있습니다.
- 엔드포인트: `GET /api/v1/categories/lowest-price`
- 응답 예시:
```json
{
  "categories": [
    {
      "category": "상의",
      "brand": "C",
      "price": 10000
    }
    // ... 다른 카테고리들
  ],
  "totalPrice": 34100
}
```

### 2. 단일 브랜드 최저가 조회 API
한 브랜드에서 모든 카테고리 상품을 구매할 때 어떤 브랜드가 가장 저렴한지 알려드립니다.
- 엔드포인트: `GET /api/v1/brands/lowest-price`
- 응답 예시:
```json
{
  "brandName": "D",
  "categories": [
    {
      "categoryName": "상의",
      "price": 10100
    }
    // ... 다른 카테고리들
  ],
  "totalPrice": 36100
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

## 향후 개선 계획

더 나은 서비스를 위해 다음과 같은 개선을 계획하면 더 좋을것 같습니다:

1. **테스트 강화**
   - 단위 테스트 커버리지 향상
   - 통합 테스트 시나리오 추가
   - 성능 테스트 도입

2. **API 문서화 개선**
   - Swagger UI 적용
   - Spring Rest Docs를 통한 정확한 문서화

3. **프론트엔드 개발**
   - 사용자 친화적인 웹 인터페이스 구현
   - 반응형 디자인 적용

4. **성능 최적화**
   - 캐시 적용 범위 확대
   - 쿼리 최적화
   - 대용량 트래픽 대비

5. **운영 환경 개선**
   - 모니터링 시스템 구축
   - 로깅 체계 개선
   - 장애 대응 전략 수립

위 개선사항들은 실제 서비스 운영 경험을 바탕으로 지속적으로 발전시켜 나갈 예정입니다.

## API 문서

구현된 API들을 자세히 설명드리겠습니다.

### 1. 카테고리별 최저가 조회 API
각 카테고리별로 가장 저렴한 브랜드와 가격을 확인하실 수 있습니다.
- 엔드포인트: `GET /api/v1/categories/lowest-price`
- 응답 예시:
```json
{
  "categories": [
    {
      "category": "상의",
      "brand": "C",
      "price": 10000
    }
    // ... 다른 카테고리들
  ],
  "totalPrice": 34100
}
```

### 2. 단일 브랜드 최저가 조회 API
한 브랜드에서 모든 카테고리 상품을 구매할 때 어떤 브랜드가 가장 저렴한지 알려드립니다.
- 엔드포인트: `GET /api/v1/brands/lowest-price`
- 응답 예시:
```json
{
  "brandName": "D",
  "categories": [
    {
      "categoryName": "상의",
      "price": 10100
    }
    // ... 다른 카테고리들
  ],
  "totalPrice": 36100
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

## 향후 개선 계획

더 나은 서비스를 위해 다음과 같은 개선을 계획하면 더 좋을것 같습니다:

1. **테스트 강화**
   - 단위 테스트 커버리지 향상
   - 통합 테스트 시나리오 추가
   - 성능 테스트 도입

2. **API 문서화 개선**
   - Swagger UI 적용
   - Spring Rest Docs를 통한 정확한 문서화

3. **프론트엔드 개발**
   - 사용자 친화적인 웹 인터페이스 구현
   - 반응형 디자인 적용

4. **성능 최적화**
   - 캐시 적용 범위 확대
   - 쿼리 최적화
   - 대용량 트래픽 대비

5. **운영 환경 개선**
   - 모니터링 시스템 구축
   - 로깅 체계 개선
   - 장애 대응 전략 수립

위 개선사항들은 실제 서비스 운영 경험을 바탕으로 지속적으로 발전시켜 나갈 예정입니다.

## API 문서

구현된 API들을 자세히 설명드리겠습니다.

### 1. 카테고리별 최저가 조회 API
각 카테고리별로 가장 저렴한 브랜드와 가격을 확인하실 수 있습니다.
- 엔드포인트: `GET /api/v1/categories/lowest-price`
- 응답 예시:
```json
{
  "categories": [
    {
      "category": "상의",
      "brand": "C",
      "price": 10000
    }
    // ... 다른 카테고리들
  ],
  "totalPrice": 34100
}
```

### 2. 단일 브랜드 최저가 조회 API
한 브랜드에서 모든 카테고리 상품을 구매할 때 어떤 브랜드가 가장 저렴한지 알려드립니다.
- 엔드포인트: `GET /api/v1/brands/lowest-price`
- 응답 예시:
```json
{
  "brandName": "D",
  "categories": [
    {
      "categoryName": "상의",
      "price": 10100
    }
    // ... 다른 카테고리들
  ],
  "totalPrice": 36100
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

## 향후 개선 계획

더 나은 서비스를 위해 다음과 같은 개선을 계획하면 더 좋을것 같습니다:

1. **테스트 강화**
   - 단위 테스트 커버리지 향상
   - 통합 테스트 시나리오 추가
   - 성능 테스트 도입

2. **API 문서화 개선**
   - Swagger UI 적용
   - Spring Rest Docs를 통한 정확한 문서화

3. **프론트엔드 개발**
   - 사용자 친화적인 웹 인터페이스 구현
   - 반응형 디자인 적용

4. **성능 최적화**
   - 캐시 적용 범위 확대
   - 쿼리 최적화
   - 대용량 트래픽 대비

5. **운영 환경 개선**
   - 모니터링 시스템 구축
   - 로깅 체계 개선
   - 장애 대응 전략 수립

위 개선사항들은 실제 서비스 운영 경험을 바탕으로 지속적으로 발전시켜 나갈 예정입니다.

## API 문서

구현된 API들을 자세히 설명드리겠습니다.

### 1. 카테고리별 최저가 조회 API
각 카테고리별로 가장 저렴한 브랜드와 가격을 확인하실 수 있습니다.
- 엔드포인트: `GET /api/v1/categories/lowest-price`
- 응답 예시:
```json
{
  "categories": [
    {
      "category": "상의",
      "brand": "C",
      "price": 10000
    }
    // ... 다른 카테고리들
  ],
  "totalPrice": 34100
}
```

### 2. 단일 브랜드 최저가 조회 API
한 브랜드에서 모든 카테고리 상품을 구매할 때 어떤 브랜드가 가장 저렴한지 알려드립니다.
- 엔드포인트: `GET /api/v1/brands/lowest-price`
- 응답 예시:
```json
{
  "brandName": "D",
  "categories": [
    {
      "categoryName": "상의",
      "price": 10100
    }
    // ... 다른 카테고리들
  ],
  "totalPrice": 36100
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

## 향후 개선 계획

더 나은 서비스를 위해 다음과 같은 개선을 계획하면 더 좋을것 같습니다:

1. **테스트 강화**
   - 단위 테스트 커버리지 향상
   - 통합 테스트 시나리오 추가
   - 성능 테스트 도입

2. **API 문서화 개선**
   - Swagger UI 적용
   - Spring Rest Docs를 통한 정확한 문서화

3. **프론트엔드 개발**
   - 사용자 친화적인 웹 인터페이스 구현
   - 반응형 디자인 적용

4. **성능 최적화**
   - 캐시 적용 범위 확대
   - 쿼리 최적화
   - 대용량 트래픽 대비

5. **운영 환경 개선**
   - 모니터링 시스템 구축
   - 로깅 체계 개선
   - 장애 대응 전략 수립

위 개선사항들은 실제 서비스 운영 경험을 바탕으로 지속적으로 발전시켜 나갈 예정입니다.

## API 문서

구현된 API들을 자세히 설명드리겠습니다.

### 1. 카테고리별 최저가 조회 API
각 카테고리별로 가장 저렴한 브랜드와 가격을 확인하실 수 있습니다.
- 엔드포인트: `GET /api/v1/categories/lowest-price`
- 응답 예시:
```json
{
  "categories": [
    {
      "category": "상의",
      "brand": "C",
      "price": 10000
    }
    // ... 다른 카테고리들
  ],
  "totalPrice": 34100
}
```

### 2. 단일 브랜드 최저가 조회 API
한 브랜드에서 모든 카테고리 상품을 구매할 때 어떤 브랜드가 가장 저렴한지 알려드립니다.
- 엔드포인트: `GET /api/v1/brands/lowest-price`
- 응답 예시:
```json
{
  "brandName": "D",
  "categories": [
    {
      "categoryName": "상의",
      "price": 10100
    }
    // ... 다른 카테고리들
  ],
  "totalPrice": 36100
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
# Musinsa Assignment

무신사 코디 상품 구매 서비스 백엔드 API 구현 과제

## 구현 범위

1. 카테고리별 최저가격 브랜드와 총액 조회 API
2. 단일 브랜드 전체 카테고리 구매 시 최저가격 브랜드 조회 API
3. 특정 카테고리의 최저/최고가격 브랜드 조회 API
4. 브랜드 및 상품 관리(CRUD) API

## 기술 스택

- Java 17
- Spring Boot 3.x
- Spring Data JPA
- H2 Database
- Gradle

## 빌드 및 실행 방법

1. 프로젝트 클론
```bash
git clone [repository-url]
```

2. 프로젝트 빌드
```bash
./gradlew build
```

3. 애플리케이션 실행
```bash
./gradlew bootRun
```

## API 문서

API 문서는 애플리케이션 실행 후 다음 URL에서 확인할 수 있습니다:
```
http://localhost:8080/swagger-ui.html
```

## 테스트 실행

```bash
./gradlew test
```

## 프로젝트 구조

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── musinsa/
│   │           ├── controller/    # API 컨트롤러
│   │           ├── service/       # 비즈니스 로직
│   │           ├── repository/    # 데이터 접근 계층
│   │           ├── domain/        # 도메인 모델
│   │           └── dto/           # 데이터 전송 객체
│   └── resources/
│       ├── application.yml        # 애플리케이션 설정
│       └── data.sql              # 초기 데이터
└── test/                         # 테스트 코드
```
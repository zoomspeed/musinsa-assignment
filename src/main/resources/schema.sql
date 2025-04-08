CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    display_order INT NOT NULL,
    description VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS brands (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    price INT NOT NULL,
    category_id BIGINT NOT NULL,
    brand_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id),
    FOREIGN KEY (brand_id) REFERENCES brands(id)
);

CREATE TABLE IF NOT EXISTS product_view (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(100) NOT NULL,
    brand_id BIGINT NOT NULL,
    brand_name VARCHAR(100) NOT NULL,
    category_id BIGINT NOT NULL,
    category_code VARCHAR(100) NOT NULL,
    category_name VARCHAR(100) NOT NULL,
    price INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE IF NOT EXISTS brand_view (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 카테고리 테이블 인덱스
CREATE INDEX IF NOT EXISTS idx_category_code ON categories(code);
CREATE INDEX IF NOT EXISTS idx_display_order ON categories(display_order);

-- 브랜드 테이블 인덱스
CREATE INDEX IF NOT EXISTS idx_brand_name ON brands(name);

-- 상품 테이블 인덱스
CREATE INDEX IF NOT EXISTS idx_category_price ON products(category_id, price);
CREATE INDEX IF NOT EXISTS idx_brand_price ON products(brand_id, price);
CREATE INDEX IF NOT EXISTS idx_price ON products(price);

-- 상품 뷰 테이블 인덱스
CREATE INDEX IF NOT EXISTS idx_pv_category_price ON product_view(category_code, price);
CREATE INDEX IF NOT EXISTS idx_pv_brand_price ON product_view(brand_name, price);
CREATE INDEX IF NOT EXISTS idx_pv_product_id ON product_view(product_id);
CREATE INDEX IF NOT EXISTS idx_pv_price ON product_view(price);

-- 브랜드 뷰 테이블 인덱스
CREATE INDEX IF NOT EXISTS idx_bv_name ON brand_view(name);
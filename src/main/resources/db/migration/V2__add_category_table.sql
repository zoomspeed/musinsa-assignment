-- 카테고리 테이블 생성
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    display_order INT NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 기존 products 테이블의 category 컬럼을 category_id로 변경
ALTER TABLE products DROP COLUMN category;
ALTER TABLE products ADD COLUMN category_id BIGINT NOT NULL;
ALTER TABLE products ADD CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES categories(id);

-- 기존 product_view 테이블의 category 컬럼을 category_id로 변경
ALTER TABLE product_view DROP COLUMN category;
ALTER TABLE product_view ADD COLUMN category_id BIGINT NOT NULL;
ALTER TABLE product_view ADD CONSTRAINT fk_product_view_category FOREIGN KEY (category_id) REFERENCES categories(id); 
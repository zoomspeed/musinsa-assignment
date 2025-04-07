-- 카테고리 초기 데이터 삽입
INSERT INTO categories (code, name, display_order, description) VALUES
('TOP', '상의', 1, '상의 카테고리입니다.'),
('OUTER', '아우터', 2, '아우터 카테고리입니다.'),
('BOTTOM', '바지', 3, '하의 카테고리입니다.'),
('SHOES', '스니커즈', 4, '신발 카테고리입니다.'),
('BAG', '가방', 5, '가방 카테고리입니다.'),
('HAT', '모자', 6, '모자 카테고리입니다.'),
('SOCKS', '양말', 7, '양말 카테고리입니다.'),
('ACCESSORY', '액세서리', 8, '액세서리 카테고리입니다.');

-- 기존 products 테이블의 데이터를 새로운 category_id로 업데이트
UPDATE products p
JOIN categories c ON p.category = c.code
SET p.category_id = c.id;

-- 기존 product_view 테이블의 데이터를 새로운 category_id로 업데이트
UPDATE product_view pv
JOIN categories c ON pv.category = c.code
SET pv.category_id = c.id; 
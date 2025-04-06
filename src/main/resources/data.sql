-- 카테고리 초기 데이터
INSERT INTO categories (code, name, display_order, description) VALUES
('TOP', '상의', 1, '상의 카테고리입니다.'),
('OUTER', '아우터', 2, '아우터 카테고리입니다.'),
('BOTTOM', '바지', 3, '하의 카테고리입니다.'),
('SHOES', '스니커즈', 4, '신발 카테고리입니다.'),
('BAG', '가방', 5, '가방 카테고리입니다.'),
('HAT', '모자', 6, '모자 카테고리입니다.'),
('SOCKS', '양말', 7, '양말 카테고리입니다.'),
('ACCESSORY', '액세서리', 8, '액세서리 카테고리입니다.');

-- 브랜드 데이터 추가
INSERT INTO brands (name) VALUES
('A'),
('B'),
('C'),
('D'),
('E'),
('F'),
('G'),
('H'),
('I');

INSERT INTO brand_view (name) VALUES
('A'),
('B'),
('C'),
('D'),
('E'),
('F'),
('G'),
('H'),
('I');

-- 제품 데이터 추가
INSERT INTO products (name, price, category_id, brand_id) VALUES
('상의 A', 11200, (SELECT id FROM categories WHERE code = 'TOP'), (SELECT id FROM brands WHERE name = 'A')),
('아우터 A', 5500, (SELECT id FROM categories WHERE code = 'OUTER'), (SELECT id FROM brands WHERE name = 'A')),
('바지 A', 4200, (SELECT id FROM categories WHERE code = 'BOTTOM'), (SELECT id FROM brands WHERE name = 'A')),
('스니커즈 A', 9000, (SELECT id FROM categories WHERE code = 'SHOES'), (SELECT id FROM brands WHERE name = 'A')),
('가방 A', 2000, (SELECT id FROM categories WHERE code = 'BAG'), (SELECT id FROM brands WHERE name = 'A')),
('오지 A', 1700, (SELECT id FROM categories WHERE code = 'HAT'), (SELECT id FROM brands WHERE name = 'A')),
('양말 A', 1800, (SELECT id FROM categories WHERE code = 'SOCKS'), (SELECT id FROM brands WHERE name = 'A')),
('액세서리 A', 2300, (SELECT id FROM categories WHERE code = 'ACCESSORY'), (SELECT id FROM brands WHERE name = 'A')),

('상의 B', 10500, (SELECT id FROM categories WHERE code = 'TOP'), (SELECT id FROM brands WHERE name = 'B')),
('아우터 B', 5900, (SELECT id FROM categories WHERE code = 'OUTER'), (SELECT id FROM brands WHERE name = 'B')),
('바지 B', 3800, (SELECT id FROM categories WHERE code = 'BOTTOM'), (SELECT id FROM brands WHERE name = 'B')),
('스니커즈 B', 9100, (SELECT id FROM categories WHERE code = 'SHOES'), (SELECT id FROM brands WHERE name = 'B')),
('가방 B', 2100, (SELECT id FROM categories WHERE code = 'BAG'), (SELECT id FROM brands WHERE name = 'B')),
('오지 B', 2000, (SELECT id FROM categories WHERE code = 'HAT'), (SELECT id FROM brands WHERE name = 'B')),
('양말 B', 2000, (SELECT id FROM categories WHERE code = 'SOCKS'), (SELECT id FROM brands WHERE name = 'B')),
('액세서리 B', 2200, (SELECT id FROM categories WHERE code = 'ACCESSORY'), (SELECT id FROM brands WHERE name = 'B')),

('상의 C', 10000, (SELECT id FROM categories WHERE code = 'TOP'), (SELECT id FROM brands WHERE name = 'C')),
('아우터 C', 6200, (SELECT id FROM categories WHERE code = 'OUTER'), (SELECT id FROM brands WHERE name = 'C')),
('바지 C', 3300, (SELECT id FROM categories WHERE code = 'BOTTOM'), (SELECT id FROM brands WHERE name = 'C')),
('스니커즈 C', 9200, (SELECT id FROM categories WHERE code = 'SHOES'), (SELECT id FROM brands WHERE name = 'C')),
('가방 C', 2200, (SELECT id FROM categories WHERE code = 'BAG'), (SELECT id FROM brands WHERE name = 'C')),
('오지 C', 1900, (SELECT id FROM categories WHERE code = 'HAT'), (SELECT id FROM brands WHERE name = 'C')),
('양말 C', 2200, (SELECT id FROM categories WHERE code = 'SOCKS'), (SELECT id FROM brands WHERE name = 'C')),
('액세서리 C', 2100, (SELECT id FROM categories WHERE code = 'ACCESSORY'), (SELECT id FROM brands WHERE name = 'C')),

('상의 D', 10100, (SELECT id FROM categories WHERE code = 'TOP'), (SELECT id FROM brands WHERE name = 'D')),
('아우터 D', 5100, (SELECT id FROM categories WHERE code = 'OUTER'), (SELECT id FROM brands WHERE name = 'D')),
('바지 D', 3000, (SELECT id FROM categories WHERE code = 'BOTTOM'), (SELECT id FROM brands WHERE name = 'D')),
('스니커즈 D', 9500, (SELECT id FROM categories WHERE code = 'SHOES'), (SELECT id FROM brands WHERE name = 'D')),
('가방 D', 2500, (SELECT id FROM categories WHERE code = 'BAG'), (SELECT id FROM brands WHERE name = 'D')),
('오지 D', 1500, (SELECT id FROM categories WHERE code = 'HAT'), (SELECT id FROM brands WHERE name = 'D')),
('양말 D', 2400, (SELECT id FROM categories WHERE code = 'SOCKS'), (SELECT id FROM brands WHERE name = 'D')),
('액세서리 D', 2000, (SELECT id FROM categories WHERE code = 'ACCESSORY'), (SELECT id FROM brands WHERE name = 'D')),

('상의 E', 10700, (SELECT id FROM categories WHERE code = 'TOP'), (SELECT id FROM brands WHERE name = 'E')),
('아우터 E', 5000, (SELECT id FROM categories WHERE code = 'OUTER'), (SELECT id FROM brands WHERE name = 'E')),
('바지 E', 3800, (SELECT id FROM categories WHERE code = 'BOTTOM'), (SELECT id FROM brands WHERE name = 'E')),
('스니커즈 E', 9900, (SELECT id FROM categories WHERE code = 'SHOES'), (SELECT id FROM brands WHERE name = 'E')),
('가방 E', 2300, (SELECT id FROM categories WHERE code = 'BAG'), (SELECT id FROM brands WHERE name = 'E')),
('오지 E', 1800, (SELECT id FROM categories WHERE code = 'HAT'), (SELECT id FROM brands WHERE name = 'E')),
('양말 E', 2100, (SELECT id FROM categories WHERE code = 'SOCKS'), (SELECT id FROM brands WHERE name = 'E')),
('액세서리 E', 2100, (SELECT id FROM categories WHERE code = 'ACCESSORY'), (SELECT id FROM brands WHERE name = 'E')),

('상의 F', 11200, (SELECT id FROM categories WHERE code = 'TOP'), (SELECT id FROM brands WHERE name = 'F')),
('아우터 F', 7200, (SELECT id FROM categories WHERE code = 'OUTER'), (SELECT id FROM brands WHERE name = 'F')),
('바지 F', 4000, (SELECT id FROM categories WHERE code = 'BOTTOM'), (SELECT id FROM brands WHERE name = 'F')),
('스니커즈 F', 9300, (SELECT id FROM categories WHERE code = 'SHOES'), (SELECT id FROM brands WHERE name = 'F')),
('가방 F', 2100, (SELECT id FROM categories WHERE code = 'BAG'), (SELECT id FROM brands WHERE name = 'F')),
('오지 F', 1600, (SELECT id FROM categories WHERE code = 'HAT'), (SELECT id FROM brands WHERE name = 'F')),
('양말 F', 2300, (SELECT id FROM categories WHERE code = 'SOCKS'), (SELECT id FROM brands WHERE name = 'F')),
('액세서리 F', 1900, (SELECT id FROM categories WHERE code = 'ACCESSORY'), (SELECT id FROM brands WHERE name = 'F')),

('상의 G', 10500, (SELECT id FROM categories WHERE code = 'TOP'), (SELECT id FROM brands WHERE name = 'G')),
('아우터 G', 5800, (SELECT id FROM categories WHERE code = 'OUTER'), (SELECT id FROM brands WHERE name = 'G')),
('바지 G', 3900, (SELECT id FROM categories WHERE code = 'BOTTOM'), (SELECT id FROM brands WHERE name = 'G')),
('스니커즈 G', 9000, (SELECT id FROM categories WHERE code = 'SHOES'), (SELECT id FROM brands WHERE name = 'G')),
('가방 G', 2200, (SELECT id FROM categories WHERE code = 'BAG'), (SELECT id FROM brands WHERE name = 'G')),
('오지 G', 1700, (SELECT id FROM categories WHERE code = 'HAT'), (SELECT id FROM brands WHERE name = 'G')),
('양말 G', 2100, (SELECT id FROM categories WHERE code = 'SOCKS'), (SELECT id FROM brands WHERE name = 'G')),
('액세서리 G', 2000, (SELECT id FROM categories WHERE code = 'ACCESSORY'), (SELECT id FROM brands WHERE name = 'G')),

('상의 H', 10900, (SELECT id FROM categories WHERE code = 'TOP'), (SELECT id FROM brands WHERE name = 'H')),
('아우터 H', 6300, (SELECT id FROM categories WHERE code = 'OUTER'), (SELECT id FROM brands WHERE name = 'H')),
('바지 H', 3100, (SELECT id FROM categories WHERE code = 'BOTTOM'), (SELECT id FROM brands WHERE name = 'H')),
('스니커즈 H', 9700, (SELECT id FROM categories WHERE code = 'SHOES'), (SELECT id FROM brands WHERE name = 'H')),
('가방 H', 2100, (SELECT id FROM categories WHERE code = 'BAG'), (SELECT id FROM brands WHERE name = 'H')),
('오지 H', 1600, (SELECT id FROM categories WHERE code = 'HAT'), (SELECT id FROM brands WHERE name = 'H')),
('양말 H', 2000, (SELECT id FROM categories WHERE code = 'SOCKS'), (SELECT id FROM brands WHERE name = 'H')),
('액세서리 H', 2000, (SELECT id FROM categories WHERE code = 'ACCESSORY'), (SELECT id FROM brands WHERE name = 'H')),

('상의 I', 11400, (SELECT id FROM categories WHERE code = 'TOP'), (SELECT id FROM brands WHERE name = 'I')),
('아우터 I', 6700, (SELECT id FROM categories WHERE code = 'OUTER'), (SELECT id FROM brands WHERE name = 'I')),
('바지 I', 3200, (SELECT id FROM categories WHERE code = 'BOTTOM'), (SELECT id FROM brands WHERE name = 'I')),
('스니커즈 I', 9500, (SELECT id FROM categories WHERE code = 'SHOES'), (SELECT id FROM brands WHERE name = 'I')),
('가방 I', 2400, (SELECT id FROM categories WHERE code = 'BAG'), (SELECT id FROM brands WHERE name = 'I')),
('오지 I', 1700, (SELECT id FROM categories WHERE code = 'HAT'), (SELECT id FROM brands WHERE name = 'I')),
('양말 I', 1700, (SELECT id FROM categories WHERE code = 'SOCKS'), (SELECT id FROM brands WHERE name = 'I')),
('액세서리 I', 2400, (SELECT id FROM categories WHERE code = 'ACCESSORY'), (SELECT id FROM brands WHERE name = 'I'));

INSERT INTO product_view (product_id, brand_id, brand_name, category_id, price)
SELECT
    p.id AS product_id,
    b.id AS brand_id,
    b.name AS brand_name,
    c.id AS category_id,
    p.price
FROM
    products p
        JOIN
    brands b ON p.brand_id = b.id
        JOIN
    categories c ON p.category_id = c.id;
-- =====================================================
-- STATIONARY MANAGEMENT SYSTEM DATABASE SEED SCRIPT
-- DATABASE: stationary_db
-- =====================================================

-- 1️⃣ CREATE DATABASE AND USE IT
SET SQL_SAFE_UPDATES = 0;
DROP DATABASE IF EXISTS stationary_db;
CREATE DATABASE stationary_db;
USE stationary_db;

-- 2️⃣ CREATE TABLES
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL
);

CREATE TABLE items (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    category VARCHAR(50) NOT NULL,
    quantity INT NOT NULL,
    threshold INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL
);

CREATE TABLE sales (
    sale_id INT AUTO_INCREMENT PRIMARY KEY,
    item_id INT NOT NULL,
    quantity INT NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    sale_date DATETIME NOT NULL,
    FOREIGN KEY (item_id) REFERENCES items(item_id) ON DELETE CASCADE
);

-- 3️⃣ INSERT ADMIN USER
INSERT INTO users (username, password)
VALUES ('admin', 'admin123');

-- 4️⃣ INSERT UNIQUE STATIONARY ITEMS
-- Each item name is unique to avoid redundancy
INSERT INTO items (name, category, quantity, threshold, price, status) VALUES
    ('Ball Point Pen',          'Writing Instruments', 250, 100, 10.50,  'Available'),
    ('Gel Pen',                 'Writing Instruments', 120, 100, 18.75,  'Available'),
    ('Mechanical Pencil',       'Writing Instruments',  80,  60, 24.90,  'Available'),
    ('HB Pencil',               'Writing Instruments',  55,  80,  6.20,  'Low Stock'),
    ('Highlighter Pack',        'Highlighting',         45,  50, 32.40,  'Low Stock'),
    ('Fine Tip Marker',         'Markers',              75,  40, 15.30,  'Available'),
    ('Whiteboard Marker',       'Markers',             180, 120, 22.80,  'Available'),
    ('Artist Marker Set',       'Markers',              20,  35,120.00,  'Low Stock'),
    ('Standard Eraser',         'Accessories',         300, 150,  4.00,  'Available'),
    ('Kneaded Eraser',          'Accessories',          60,  40, 12.00,  'Available'),
    ('A4 Notebook',             'Paper Products',      210, 150, 45.00,  'Available'),
    ('Spiral Notebook',         'Paper Products',      130, 120, 38.50,  'Available'),
    ('Hardcover Register',      'Paper Products',       70,  80, 95.00,  'Low Stock'),
    ('Pocket Diary',            'Paper Products',       25,  30, 55.00,  'Low Stock'),
    ('File Folder',             'Filing Supplies',     260, 200, 14.20,  'Available'),
    ('Document File',           'Filing Supplies',     175, 150, 28.75,  'Available'),
    ('Box File',                'Filing Supplies',      90, 120, 42.60,  'Low Stock'),
    ('Transparent Folder',      'Filing Supplies',      45,  60, 18.90,  'Low Stock'),
    ('Paper Ream A4',           'Paper Products',      320, 200,180.00,  'Available'),
    ('Paper Ream Legal',        'Paper Products',      140, 160,210.00,  'Low Stock'),
    ('Bubble Envelope',         'Mailing Supplies',     95,  80, 12.75,  'Available'),
    ('Business Envelope',       'Mailing Supplies',    180, 150,  6.50,  'Available'),
    ('Packaging Tape',          'Adhesives',           220, 180, 19.90,  'Available'),
    ('Double Sided Tape',       'Adhesives',            35,  60, 33.40,  'Low Stock'),
    ('Glue Stick',              'Adhesives',           260, 200, 14.85,  'Available'),
    ('Multi Purpose Glue',      'Adhesives',            60,  80, 28.65,  'Low Stock'),
    ('30cm Ruler',              'Measuring Tools',     190, 160, 12.10,  'Available'),
    ('Metal Ruler',             'Measuring Tools',      85,  90, 22.40,  'Low Stock'),
    ('Geometry Set',            'Measuring Tools',      70,  50, 65.00,  'Available'),
    ('Stapler Medium',          'Fasteners',           140, 120, 58.75,  'Available'),
    ('Stapler Pins',            'Fasteners',           520, 400,  9.80,  'Available'),
    ('Binder Clips Set',        'Fasteners',           310, 250, 16.20,  'Available'),
    ('Paper Clips Box',         'Fasteners',           420, 300,  7.50,  'Available'),
    ('Correction Tape',         'Accessories',          95,  80, 26.40,  'Available'),
    ('Sticky Notes Pack',       'Accessories',         150, 120, 21.90,  'Available'),
    ('Desk Organizer',          'Desk Supplies',        40,  50,145.00,  'Low Stock'),
    ('Office Scissors',         'Accessories',         110, 100, 34.80,  'Available'),
    ('Paper Shredder',          'Office Equipment',     15,  20,850.00,  'Low Stock'),
    ('Laminating Pouches',      'Office Equipment',     85,  90, 55.60,  'Low Stock'),
    ('ID Card Holder',          'Accessories',         200, 150, 18.20,  'Available'),
    ('Desk Calendar',           'Desk Supplies',        55,  60, 95.00,  'Low Stock');

-- Ensure status reflects quantity vs threshold
UPDATE items
SET status = CASE
    WHEN quantity = 0 THEN 'Out of Stock'
    WHEN quantity < threshold THEN 'Low Stock'
    ELSE 'Available'
END;

-- 5️⃣ INSERT SAMPLE SALES RECORDS (REFERENCING UNIQUE ITEMS)
INSERT INTO sales (item_id, quantity, total_price, sale_date) VALUES
    (1,  20,  210.00, NOW() - INTERVAL 15 DAY),
    (3,  10,  249.00, NOW() - INTERVAL 14 DAY),
    (5,  15,  486.00, NOW() - INTERVAL 13 DAY),
    (7,  25,  570.00, NOW() - INTERVAL 12 DAY),
    (9,  40,  160.00, NOW() - INTERVAL 11 DAY),
    (11, 30, 1350.00, NOW() - INTERVAL 10 DAY),
    (13,  8,  760.00, NOW() - INTERVAL 9 DAY),
    (15, 50,  710.00, NOW() - INTERVAL 8 DAY),
    (17, 18,  766.80, NOW() - INTERVAL 7 DAY),
    (19, 60,10800.00, NOW() - INTERVAL 6 DAY),
    (21, 35,  446.25, NOW() - INTERVAL 5 DAY),
    (23, 80, 1592.00, NOW() - INTERVAL 4 DAY),
    (25, 25,  371.25, NOW() - INTERVAL 3 DAY),
    (27, 40,  484.00, NOW() - INTERVAL 2 DAY),
    (29, 15,  975.00, NOW() - INTERVAL 1 DAY),
    (31,120, 1176.00, NOW()),
    (33,200, 1500.00, NOW()),
    (35, 60, 1314.00, NOW()),
    (37, 22, 3190.00, NOW()),
    (39, 30, 1668.00, NOW());

-- Recalculate quantities after sales
UPDATE items i
JOIN (
    SELECT item_id, SUM(quantity) AS sold_qty
    FROM sales
    GROUP BY item_id
) s ON i.item_id = s.item_id
SET i.quantity = GREATEST(i.quantity - s.sold_qty, 0);

-- Update status again after deducting sales quantities
UPDATE items
SET status = CASE
    WHEN quantity = 0 THEN 'Out of Stock'
    WHEN quantity < threshold THEN 'Low Stock'
    ELSE 'Available'
END;

-- 6️⃣ SUMMARY COUNTS
SELECT 'Users' AS entity, COUNT(*) AS total FROM users
UNION ALL
SELECT 'Items', COUNT(*) FROM items
UNION ALL
SELECT 'Sales', COUNT(*) FROM sales;

-- =====================================================
-- ✅ DONE - UNIQUE ITEMS ONLY (NO DUPLICATES)
-- =====================================================

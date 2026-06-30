-- =====================================================
-- CLEANUP AND MERGE DUPLICATE ITEMS SCRIPT
-- This script merges duplicate items by combining quantities
-- Keeps the item with the lowest ID and merges data from duplicates
-- =====================================================

USE stationary_db;

-- Step 1: Show duplicates before cleanup
SELECT 'BEFORE CLEANUP - Duplicate Items:' as info;
SELECT name, COUNT(*) as count, 
       GROUP_CONCAT(item_id ORDER BY item_id) as item_ids,
       SUM(quantity) as total_quantity
FROM items 
GROUP BY LOWER(TRIM(name)) 
HAVING COUNT(*) > 1 
ORDER BY count DESC;

-- Step 2: Create a temporary table to store merged data
CREATE TEMPORARY TABLE IF NOT EXISTS merged_items AS
SELECT 
    MIN(item_id) as keep_id,
    LOWER(TRIM(name)) as name_key,
    name,
    category,
    SUM(quantity) as total_quantity,
    AVG(threshold) as avg_threshold,
    AVG(price) as avg_price
FROM items
GROUP BY LOWER(TRIM(name)), category;

-- Step 3: Update the item with the lowest ID with merged data
UPDATE items i
INNER JOIN merged_items m ON i.item_id = m.keep_id
SET 
    i.quantity = m.total_quantity,
    i.threshold = ROUND(m.avg_threshold),
    i.price = ROUND(m.avg_price, 2),
    i.status = CASE 
        WHEN m.total_quantity = 0 THEN 'Out of Stock'
        WHEN m.total_quantity < ROUND(m.avg_threshold) THEN 'Low Stock'
        ELSE 'Available'
    END;

-- Step 4: Update sales records to point to the kept item
UPDATE sales s
INNER JOIN items i ON s.item_id = i.item_id
INNER JOIN merged_items m ON LOWER(TRIM(i.name)) = m.name_key AND i.item_id != m.keep_id
SET s.item_id = m.keep_id;

-- Step 5: Delete duplicate items (keeping the one with lowest ID)
DELETE i1 FROM items i1
INNER JOIN items i2 
WHERE i1.item_id > i2.item_id 
AND LOWER(TRIM(i1.name)) = LOWER(TRIM(i2.name));

-- Step 6: Drop temporary table
DROP TEMPORARY TABLE IF EXISTS merged_items;

-- Step 7: Verify duplicates are removed
SELECT 'AFTER CLEANUP - Remaining Duplicates:' as info;
SELECT name, COUNT(*) as count 
FROM items 
GROUP BY LOWER(TRIM(name)) 
HAVING COUNT(*) > 1;

-- Step 8: Show final item count
SELECT 'FINAL COUNT:' as info;
SELECT COUNT(*) as total_items FROM items;

-- Step 9: Reset AUTO_INCREMENT to continue from the highest ID
SET @max_id = (SELECT MAX(item_id) FROM items);
SET @sql = CONCAT('ALTER TABLE items AUTO_INCREMENT = ', IFNULL(@max_id, 0) + 1);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- =====================================================
-- ✅ CLEANUP AND MERGE COMPLETE
-- All duplicate items have been merged into single entries
-- Quantities have been combined
-- =====================================================


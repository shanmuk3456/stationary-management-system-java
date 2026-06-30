-- =====================================================
-- CLEANUP DUPLICATE ITEMS SCRIPT
-- This script removes duplicate items, keeping the one with the lowest ID
-- =====================================================

USE stationary_db;

-- Step 1: Show duplicates before cleanup
SELECT name, COUNT(*) as count 
FROM items 
GROUP BY LOWER(TRIM(name)) 
HAVING COUNT(*) > 1 
ORDER BY count DESC;

-- Step 2: Delete duplicate items, keeping the one with the lowest item_id
-- This will also delete related sales records due to FOREIGN KEY CASCADE
DELETE i1 FROM items i1
INNER JOIN items i2 
WHERE i1.item_id > i2.item_id 
AND LOWER(TRIM(i1.name)) = LOWER(TRIM(i2.name));

-- Step 3: Verify duplicates are removed
SELECT name, COUNT(*) as count 
FROM items 
GROUP BY LOWER(TRIM(name)) 
HAVING COUNT(*) > 1;

-- Step 4: Show final item count
SELECT COUNT(*) as total_items FROM items;

-- Step 5: Reset AUTO_INCREMENT to continue from the highest ID
SET @max_id = (SELECT MAX(item_id) FROM items);
SET @sql = CONCAT('ALTER TABLE items AUTO_INCREMENT = ', @max_id + 1);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- =====================================================
-- ✅ CLEANUP COMPLETE
-- =====================================================


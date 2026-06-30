-- Check users in the database
USE stationary_db;

-- View all users
SELECT * FROM users;

-- If no users exist, insert the admin user
-- Uncomment the line below if needed:
-- INSERT INTO users (username, password) VALUES ('admin', 'admin123');

-- To reset/update the admin password, use:
-- UPDATE users SET password = 'admin123' WHERE username = 'admin';


-- Create database
CREATE DATABASE IF NOT EXISTS stationary_db;
USE stationary_db;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(50) NOT NULL
);

-- Create items table
CREATE TABLE IF NOT EXISTS items (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50),
    quantity INT NOT NULL DEFAULT 0,
    threshold INT NOT NULL DEFAULT 10,
    price DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) DEFAULT 'Available'
);

-- Create sales table
CREATE TABLE IF NOT EXISTS sales (
    sale_id INT AUTO_INCREMENT PRIMARY KEY,
    item_id INT NOT NULL,
    quantity INT NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    sale_date DATETIME NOT NULL,
    FOREIGN KEY (item_id) REFERENCES items(item_id) ON DELETE CASCADE
);

-- Insert default admin user (username: admin, password: admin123)
INSERT INTO users (username, password) VALUES ('admin', 'admin123');

-- Insert sample items
INSERT INTO items (name, category, quantity, threshold, price, status) VALUES
('Pen', 'Writing', 50, 20, 5.00, 'Available'),
('Pencil', 'Writing', 15, 20, 3.00, 'Low Stock'),
('Notebook', 'Paper', 0, 10, 25.00, 'Out of Stock'),
('Eraser', 'Writing', 30, 15, 2.50, 'Available'),
('Ruler', 'Measuring', 5, 10, 8.00, 'Low Stock');



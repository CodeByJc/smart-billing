-- ============================================================
-- Smart Billing & Inventory Management System
-- Database Schema for MySQL
-- ============================================================

CREATE DATABASE IF NOT EXISTS smart_billing;
USE smart_billing;

-- ------------------------------------------------------------
-- Users Table
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'ADMIN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- Products Table
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    gst_percentage DECIMAL(5, 2) NOT NULL DEFAULT 18.00,
    stock_quantity INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_product_name (name),
    INDEX idx_stock_quantity (stock_quantity)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- Invoices Table
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS invoices (
    id INT AUTO_INCREMENT PRIMARY KEY,
    invoice_number VARCHAR(30) NOT NULL UNIQUE,
    customer_name VARCHAR(150) NOT NULL,
    subtotal DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    gst_amount DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    total_amount DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    payment_type ENUM('Cash', 'Online') NOT NULL DEFAULT 'Cash',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_invoice_number (invoice_number),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- Invoice Items Table
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS invoice_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    invoice_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    gst_percentage DECIMAL(5, 2) NOT NULL,
    total DECIMAL(12, 2) NOT NULL,
    CONSTRAINT fk_invoice_items_invoice
        FOREIGN KEY (invoice_id) REFERENCES invoices(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_invoice_items_product
        FOREIGN KEY (product_id) REFERENCES products(id)
        ON DELETE RESTRICT,
    INDEX idx_invoice_id (invoice_id),
    INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- Default Admin User
-- Password: admin123
-- ------------------------------------------------------------
INSERT INTO users (username, password, role) VALUES
('admin', 'admin123', 'ADMIN');

-- ------------------------------------------------------------
-- Sample Products
-- ------------------------------------------------------------
INSERT INTO products (name, description, price, gst_percentage, stock_quantity) VALUES
('Laptop Dell Inspiron 15', 'Dell Inspiron 15 with Intel Core i5, 8GB RAM, 512GB SSD', 55000.00, 18.00, 25),
('Wireless Mouse Logitech', 'Logitech M235 Wireless Mouse - Black', 799.00, 18.00, 150),
('USB-C Hub Adapter', '7-in-1 USB-C Hub with HDMI, USB 3.0, SD Card Reader', 2499.00, 18.00, 60),
('Mechanical Keyboard', 'RGB Mechanical Gaming Keyboard with Blue Switches', 3499.00, 18.00, 40),
('Monitor 24 inch Samsung', 'Samsung 24-inch Full HD IPS Monitor, 75Hz', 12500.00, 18.00, 15),
('Headphones Sony WH-1000XM5', 'Sony WH-1000XM5 Noise Cancelling Headphones', 24990.00, 18.00, 30),
('Webcam Logitech C920', 'Logitech C920 HD Pro Webcam 1080p', 7990.00, 18.00, 45),
('Printer HP LaserJet', 'HP LaserJet Pro M404dn Monochrome Laser Printer', 18500.00, 12.00, 10),
('External HDD 1TB', 'Seagate Barracuda 1TB External Hard Drive USB 3.0', 4200.00, 18.00, 70),
('Phone Case Universal', 'Universal Silicone Phone Case - Transparent', 299.00, 12.00, 200);

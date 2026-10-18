-- ============================================================
-- StudentNest Database Setup Script (MySQL / XAMPP)
-- ============================================================

CREATE DATABASE IF NOT EXISTS studentnest_db;
USE studentnest_db;

-- 1. Create users table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    user_type ENUM('Student', 'House Owner', 'Admin') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Create rooms table
CREATE TABLE IF NOT EXISTS rooms (
    id INT AUTO_INCREMENT PRIMARY KEY,
    owner_id INT NOT NULL,
    location VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    description TEXT,
    contact_number VARCHAR(20) NOT NULL,
    map_link VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 3. Insert default admin user
INSERT IGNORE INTO users (id, name, phone, username, password, user_type) 
VALUES (1, 'Admin', '01700000000', 'admin', 'admin123', 'Admin');

-- 4. Insert sample users
INSERT IGNORE INTO users (id, name, phone, username, password, user_type) VALUES
(2, 'Hasib Student', '01234567891', 'hasib_student', 'password123', 'Student'),
(3, 'Rahul Student', '01234567892', 'rahul_student', 'password123', 'Student'),
(4, 'Arman Owner', '01234567893', 'arman_owner', 'password123', 'House Owner'),
(5, 'Saief Owner', '01234567894', 'saief_owner', 'password123', 'House Owner'),
(6, 'Sabbir Owner', '01234567895', 'sabbir_owner', 'password123', 'House Owner');

-- 5. Insert sample rooms
INSERT INTO rooms (owner_id, location, price, description, contact_number, map_link) VALUES
(4, 'Khagan', 8000.00, 'Single room with attached bathroom, furnished, near DIU campus', '01711111111', 'https://maps.google.com/khagan'),
(4, 'Candgaon', 6500.00, 'Shared room, 2 beds, common kitchen, 5 min walk to DIU', '01711111111', 'https://maps.google.com/candgaon'),
(5, 'Charabag', 12000.00, 'Private room with balcony, AC, WiFi included', '01722222222', 'https://maps.google.com/charabag'),
(5, 'Kumkumari', 7500.00, 'Double sharing, fully furnished, meals available', '01722222222', 'https://maps.google.com/kumkumari');

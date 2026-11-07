CREATE DATABASE IF NOT EXISTS stock_management_db
  CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE stock_management_db;

CREATE TABLE IF NOT EXISTS roles (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(30) NOT NULL UNIQUE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  full_name VARCHAR(120) NOT NULL,
  email VARCHAR(190) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS user_roles (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  PRIMARY KEY (user_id, role_id),
  CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id)
);
CREATE TABLE IF NOT EXISTS categories (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL UNIQUE,
  description VARCHAR(500),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS products (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(150) NOT NULL,
  code VARCHAR(60) NOT NULL UNIQUE,
  description TEXT,
  unit_price DECIMAL(12,2) NOT NULL DEFAULT 0,
  quantity INT NOT NULL DEFAULT 0,
  minimum_stock INT NOT NULL DEFAULT 0,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  category_id BIGINT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT chk_product_price CHECK (unit_price >= 0),
  CONSTRAINT chk_product_quantity CHECK (quantity >= 0),
  CONSTRAINT chk_product_minimum CHECK (minimum_stock >= 0),
  CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories(id),
  INDEX idx_products_name (name),
  INDEX idx_products_quantity (quantity)
);
CREATE TABLE IF NOT EXISTS suppliers (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(150) NOT NULL,
  phone VARCHAR(40),
  email VARCHAR(190),
  address VARCHAR(500),
  company_name VARCHAR(150),
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_suppliers_name (name)
);
CREATE TABLE IF NOT EXISTS stock_transactions (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  product_id BIGINT NOT NULL,
  supplier_id BIGINT,
  performed_by BIGINT NOT NULL,
  type VARCHAR(20) NOT NULL,
  quantity INT NOT NULL,
  recipient VARCHAR(190),
  notes VARCHAR(1000),
  transaction_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT chk_transaction_quantity CHECK (quantity > 0),
  CONSTRAINT chk_transaction_type CHECK (type IN ('INCOMING','OUTGOING')),
  CONSTRAINT fk_transactions_product FOREIGN KEY (product_id) REFERENCES products(id),
  CONSTRAINT fk_transactions_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id),
  CONSTRAINT fk_transactions_user FOREIGN KEY (performed_by) REFERENCES users(id),
  INDEX idx_transactions_date_type (transaction_date, type),
  INDEX idx_transactions_product (product_id)
);
CREATE TABLE IF NOT EXISTS stock_requests (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  requested_by BIGINT NOT NULL,
  reviewed_by BIGINT,
  status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  review_notes VARCHAR(1000),
  requested_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  reviewed_at TIMESTAMP NULL,
  issued_at TIMESTAMP NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_requests_requester FOREIGN KEY (requested_by) REFERENCES users(id),
  CONSTRAINT fk_requests_reviewer FOREIGN KEY (reviewed_by) REFERENCES users(id),
  CONSTRAINT chk_request_status CHECK (status IN ('PENDING','APPROVED','REJECTED','ISSUED')),
  INDEX idx_requests_status_date (status, requested_at)
);
CREATE TABLE IF NOT EXISTS stock_request_items (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  request_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  quantity INT NOT NULL,
  CONSTRAINT chk_request_item_quantity CHECK (quantity > 0),
  CONSTRAINT fk_request_items_request FOREIGN KEY (request_id) REFERENCES stock_requests(id) ON DELETE CASCADE,
  CONSTRAINT fk_request_items_product FOREIGN KEY (product_id) REFERENCES products(id),
  UNIQUE KEY uq_request_product (request_id, product_id)
);

INSERT IGNORE INTO roles(name) VALUES ('ADMIN'), ('STOREKEEPER'), ('STAFF');
INSERT IGNORE INTO categories(name, description) VALUES
  ('Office supplies', 'General office supplies and consumables'),
  ('Electronics', 'Computers, peripherals, and electronic equipment'),
  ('Facilities', 'Building and maintenance inventory');
INSERT IGNORE INTO products(name, code, description, unit_price, quantity, minimum_stock, active, category_id)
SELECT 'A4 Copy Paper', 'DEMO-PAPER-A4', 'Sample ream of A4 copy paper', 6.50, 0, 10, TRUE, id
FROM categories WHERE name = 'Office supplies';
INSERT IGNORE INTO products(name, code, description, unit_price, quantity, minimum_stock, active, category_id)
SELECT 'Wireless Mouse', 'DEMO-MOUSE-WL', 'Sample wireless computer mouse', 24.99, 0, 5, TRUE, id
FROM categories WHERE name = 'Electronics';
INSERT IGNORE INTO products(name, code, description, unit_price, quantity, minimum_stock, active, category_id)
SELECT 'LED Bulb 9W', 'DEMO-BULB-9W', 'Sample energy-efficient light bulb', 3.75, 0, 12, TRUE, id
FROM categories WHERE name = 'Facilities';

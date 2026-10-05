-- CampusFlow database: TABLE STRUCTURE ONLY. No data is inserted here.
CREATE DATABASE IF NOT EXISTS campusflow
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE campusflow;

CREATE TABLE IF NOT EXISTS students (
  student_id     INT AUTO_INCREMENT PRIMARY KEY,
  roll_no        VARCHAR(30)  NOT NULL UNIQUE,
  full_name      VARCHAR(100) NOT NULL,
  email          VARCHAR(150) NOT NULL UNIQUE,
  phone          VARCHAR(20),
  department     VARCHAR(100),
  password_hash  VARCHAR(255) NOT NULL,
  status         ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',
  created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS services (
  service_id     INT AUTO_INCREMENT PRIMARY KEY,
  service_name   VARCHAR(100) NOT NULL UNIQUE,
  token_prefix   VARCHAR(5)   NOT NULL UNIQUE,
  description    VARCHAR(255),
  is_active      BOOLEAN NOT NULL DEFAULT TRUE,
  created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS staff (
  staff_id       INT AUTO_INCREMENT PRIMARY KEY,
  full_name      VARCHAR(100) NOT NULL,
  email          VARCHAR(150) NOT NULL UNIQUE,
  password_hash  VARCHAR(255) NOT NULL,
  role           ENUM('STAFF','ADMIN') NOT NULL DEFAULT 'STAFF',
  service_id     INT NULL,
  status         ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',
  created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_staff_service FOREIGN KEY (service_id) REFERENCES services(service_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS tokens (
  token_id       INT AUTO_INCREMENT PRIMARY KEY,
  token_number   VARCHAR(20) NOT NULL,
  student_id     INT NOT NULL,
  service_id     INT NOT NULL,
  status         ENUM('WAITING','CALLED','COMPLETED','SKIPPED','CANCELLED') NOT NULL DEFAULT 'WAITING',
  handled_by     INT NULL,
  created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  called_at      TIMESTAMP NULL,
  completed_at   TIMESTAMP NULL,
  CONSTRAINT fk_tokens_student FOREIGN KEY (student_id) REFERENCES students(student_id),
  CONSTRAINT fk_tokens_service FOREIGN KEY (service_id) REFERENCES services(service_id),
  CONSTRAINT fk_tokens_staff   FOREIGN KEY (handled_by) REFERENCES staff(staff_id),
  INDEX idx_tokens_queue (service_id, status, created_at)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS notifications (
  notification_id INT AUTO_INCREMENT PRIMARY KEY,
  student_id      INT NOT NULL,
  token_id        INT NULL,
  message         VARCHAR(255) NOT NULL,
  is_read         BOOLEAN NOT NULL DEFAULT FALSE,
  created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_notif_student FOREIGN KEY (student_id) REFERENCES students(student_id),
  CONSTRAINT fk_notif_token   FOREIGN KEY (token_id)   REFERENCES tokens(token_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS queue_history (
  history_id     INT AUTO_INCREMENT PRIMARY KEY,
  token_id       INT NOT NULL,
  staff_id       INT NULL,
  action         ENUM('CREATED','CALLED','COMPLETED','SKIPPED','CANCELLED') NOT NULL,
  action_time    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  remarks        VARCHAR(255),
  CONSTRAINT fk_hist_token FOREIGN KEY (token_id) REFERENCES tokens(token_id),
  CONSTRAINT fk_hist_staff FOREIGN KEY (staff_id) REFERENCES staff(staff_id)
) ENGINE=InnoDB;

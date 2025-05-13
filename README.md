# 💰 Java Swing Expense Tracker

A desktop-based Expense Tracker application built using **Java Swing**, **JDBC**, and **MySQL**. Users can register, log in, enter expenses, export reports to Excel (CSV), and view category-wise summaries.

---

## 🖥️ Screenshots

### 🔐 Login Screen
![image](https://github.com/user-attachments/assets/6be6375c-cd7a-4593-9723-1682527bb138)


### 🧾 Dashboard After Login
![image](https://github.com/user-attachments/assets/7e44d63b-bfad-49a8-9d22-69f232bad276)


### 📊 Exported Excel (CSV) File
![image](https://github.com/user-attachments/assets/839cf37d-66ba-4fa1-b245-414c3cae701e)


---

## 🚀 Features

- ✅ User Registration & Secure Login
- 📅 Date validation (`yyyy-MM-dd`)
- 💸 Numeric-only amount field
- 📊 Category-wise expense summary
- 📤 Export to CSV (Excel-compatible)
- 💾 MySQL Database support

---

## 🛠️ Technologies Used

- Java Swing (GUI)
- JDBC (Database connectivity)
- MySQL (Backend storage)
- NetBeans IDE
- Apache POI (for Excel export) *(optional)*

---

## 📂 Database Setup

Run these SQL commands in your MySQL server:

```sql
CREATE DATABASE expense_tracker;

USE expense_tracker;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(50) NOT NULL
);

CREATE TABLE expenses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    date DATE,
    category VARCHAR(50),
    amount DOUBLE,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

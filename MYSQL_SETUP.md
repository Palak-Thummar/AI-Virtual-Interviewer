# 🚨 IMPORTANT: MySQL Database Setup Required

## The Problem

Your backend tried to start but failed with:
```
java.net.ConnectException: Connection refused: getsockopt
```

**Reason**: MySQL database is not running or not properly configured.

---

## Solution: Set Up MySQL

### Step 1: Install MySQL (if not already installed)

**Download from**: https://dev.mysql.com/downloads/mysql/

Choose **MySQL Community Server** for your Windows version

### Step 2: Start MySQL Service

**Option A - Using Windows Services:**
1. Press `Win + R`
2. Type: `services.msc`
3. Find "MySQL80" or "MySQL"
4. Right-click → Start

**Option B - Using Command Line:**
```bash
mysql.server start
```

**Verify MySQL is Running:**
```bash
mysql -u root -p
```
- Enter password
- If you see `mysql>` prompt → MySQL is running ✅

### Step 3: Create Database

In MySQL prompt, paste this:

```sql
CREATE DATABASE IF NOT EXISTS ai_virtual_interviewer;
```

Verify it was created:
```sql
SHOW DATABASES;
```

You should see: `ai_virtual_interviewer` ✅

### Step 4: Update Backend Configuration

Edit this file:
`backend/src/main/resources/application.yml`

Make sure it has:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ai_virtual_interviewer
    username: root
    password: YOUR_MYSQL_PASSWORD  # ← Match your MySQL root password
```

---

## Now Try Starting Backend Again

**Terminal 1:**
```bash
cd backend
mvn spring-boot:run
```

**Expected Output:**
```
:: Spring Boot :: (v3.2.0)
Started VirtualInterviewerApplication in X seconds
Tomcat initialized with port 8080
```

✅ If you see this, backend is running!

---

## Then Start Frontend

**Terminal 2:**
```bash
cd frontend
npm start
```

Opens browser at: http://localhost:3000

---

## Common Issues

**Issue**: "Access denied for user 'root'@'localhost'"
- Your MySQL password is wrong
- Update it in `application.yml`

**Issue**: "Unknown database 'ai_virtual_interviewer'"
- Run: `CREATE DATABASE ai_virtual_interviewer;` again

**Issue**: "Can't connect to MySQL server on 'localhost'"
- MySQL is not running
- Start it using Services or command line

---

## Quick Check

Test MySQL connection:
```bash
mysql -u root -p -h localhost -e "SELECT 1"
```

If you see: `1` → MySQL is working! ✅

---

**Next Steps**: Once both backend & frontend are running, see START_HERE.md

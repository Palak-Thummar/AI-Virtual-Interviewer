# Configuration Templates

## Backend Configuration (application.yml)

Place this file at: `backend/src/main/resources/application.yml`

```yaml
spring:
  application:
    name: ai-virtual-interviewer
    description: AI-Powered Virtual Interviewer for Placement Preparation
  
  # Database Configuration
  datasource:
    url: jdbc:mysql://localhost:3306/ai_virtual_interviewer
    username: root
    password: your_mysql_password
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 20000
  
  # JPA/Hibernate Configuration
  jpa:
    database-platform: org.hibernate.dialect.MySQL8Dialect
    hibernate:
      ddl-auto: update  # Use 'validate' in production
    show-sql: false
    properties:
      hibernate:
        format_sql: true
        use_sql_comments: true
        jdbc:
          batch_size: 20
          fetch_size: 50
        order_inserts: true
        order_updates: true
  
  # File Upload Configuration
  servlet:
    multipart:
      max-file-size: 50MB
      max-request-size: 50MB
  
  # Server Configuration
  server:
    port: 8080
    servlet:
      context-path: /api
    error:
      include-message: always
      include-binding-errors: always

# JWT Configuration
jwt:
  secret: your_super_secret_key_minimum_32_characters_long_recommended
  expiration: 86400000  # 24 hours in milliseconds
  refresh-expiration: 604800000  # 7 days in milliseconds

# OpenAI Configuration
openai:
  api-key: sk-your-actual-openai-api-key-here
  model: gpt-4  # or gpt-3.5-turbo for faster/cheaper
  temperature: 0.7
  timeout: 30  # seconds
  max-retries: 3

# Logging Configuration
logging:
  level:
    root: INFO
    com.virtualinterviewer: DEBUG
    org.springframework.web: DEBUG
    org.hibernate: INFO
  pattern:
    console: "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/application.log
    max-size: 10MB
    max-history: 10

# Actuator Configuration (for monitoring)
management:
  endpoints:
    web:
      exposure:
        include: health,metrics
  endpoint:
    health:
      show-details: always
```

---

## Frontend Environment Configuration (.env)

Place this file at: `frontend/.env` (create if doesn't exist)

```
# API Configuration
REACT_APP_API_URL=http://localhost:8080/api
REACT_APP_API_TIMEOUT=30000

# Environment
REACT_APP_ENV=development

# Feature Flags
REACT_APP_ENABLE_ANALYTICS=true
REACT_APP_ENABLE_VOICE=true
REACT_APP_ENABLE_ADMIN=true

# Chat Configuration
REACT_APP_CHAT_TIMEOUT=60000

# Storage
REACT_APP_STORAGE_PREFIX=aivi_
```

---

## MySQL Initial Setup Script

Run this after creating the database:

```sql
-- Create user (if using different credentials)
CREATE USER 'aivi_user'@'localhost' IDENTIFIED BY 'aivi_password';
GRANT ALL PRIVILEGES ON ai_virtual_interviewer.* TO 'aivi_user'@'localhost';
FLUSH PRIVILEGES;

-- Create tables (will be auto-created by Hibernate, but here's reference)
USE ai_virtual_interviewer;

-- Verify tables are created
SHOW TABLES;

-- Check table structure
DESC users;
DESC interview_questions;
DESC interviews;
DESC answers;
DESC feedback;
DESC analytics;
```

---

## Production Configuration

For production deployment, use this configuration:

### Production application.yml

```yaml
spring:
  application:
    name: ai-virtual-interviewer
  
  datasource:
    url: jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}
    username: ${DB_USER}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 10
  
  jpa:
    hibernate:
      ddl-auto: validate  # IMPORTANT: Don't use update in production
    show-sql: false
    properties:
      hibernate:
        format_sql: false
  
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB

server:
  port: ${PORT:8080}
  compression:
    enabled: true
    min-response-size: 1024
  servlet:
    context-path: /api

jwt:
  secret: ${JWT_SECRET}  # Set via environment variable
  expiration: 86400000

openai:
  api-key: ${OPENAI_API_KEY}  # Set via environment variable
  model: gpt-4
  timeout: 30

logging:
  level:
    root: WARN
    com.virtualinterviewer: INFO
```

### Production Environment Variables (Heroku example)

```bash
# Set these variables in your deployment platform

heroku config:set DB_HOST=your-database-host
heroku config:set DB_PORT=3306
heroku config:set DB_NAME=ai_virtual_interviewer
heroku config:set DB_USER=your_user
heroku config:set DB_PASSWORD=your_password
heroku config:set JWT_SECRET=your_very_long_secure_secret_key
heroku config:set OPENAI_API_KEY=sk-your-openai-key
heroku config:set SPRING_PROFILES_ACTIVE=prod
```

---

## Maven Settings (pom.xml reference)

Key dependencies are already in pom.xml, but here are versions:

```xml
<!-- Java Version -->
<properties>
  <java.version>17</java.version>
</properties>

<!-- Spring Boot Version: 3.2.0 -->
<!-- MySQL Connector: 8.0.33 -->
<!-- JWT: 0.12.3 -->
<!-- OpenAI: 0.14.0 -->
<!-- Lombok: Latest -->
```

---

## Docker Setup (Optional)

If you want to run MySQL in Docker:

```dockerfile
# docker-compose.yml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: ai-interviewer-db
    environment:
      MYSQL_ROOT_PASSWORD: root_password
      MYSQL_DATABASE: ai_virtual_interviewer
      MYSQL_USER: aivi_user
      MYSQL_PASSWORD: aivi_password
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      timeout: 20s
      retries: 10

volumes:
  mysql_data:
```

Run with:
```bash
docker-compose up -d
```

---

## OpenAI API Key Setup

1. **Create Account**
   - Go to https://platform.openai.com/signup
   - Sign up with email or Google account

2. **Get API Key**
   - Visit https://platform.openai.com/api-keys
   - Click "Create new secret key"
   - Copy the key (starts with `sk-`)

3. **Add to Configuration**
   - Add to `application.yml`: `openai.api-key: sk-your-key`
   - Or set environment variable: `OPENAI_API_KEY=sk-your-key`

4. **Check Billing**
   - Visit https://platform.openai.com/account/billing/overview
   - Ensure you have credits or payment method set up

---

## IDE Configuration (IntelliJ)

1. **Open Project**
   - File → Open → Select `backend` folder

2. **Configure JDK**
   - File → Project Structure → Project → Set SDK to Java 17

3. **Enable Annotation Processing**
   - File → Settings → Build, Execution, Deployment → Compiler → Annotation Processors
   - Check "Enable annotation processing"

4. **Maven**
   - File → Settings → Build, Execution, Deployment → Build Tools → Maven
   - Ensure correct Maven home is selected

---

## VS Code Configuration (Optional)

Create `.vscode/settings.json`:

```json
{
  "java.home": "/path/to/java17",
  "java.configuration.runtimes": [
    {
      "name": "JavaSE-17",
      "path": "/path/to/java17",
      "default": true
    }
  ],
  "maven.executable.path": "/path/to/maven/bin/mvn",
  "[java]": {
    "editor.defaultFormatter": "redhat.java",
    "editor.formatOnSave": true
  }
}
```

---

## Useful Commands

### Backend

```bash
# Build
mvn clean package

# Run tests
mvn test

# Run specific test class
mvn test -Dtest=ClassName

# Skip tests during build
mvn clean package -DskipTests

# View dependencies
mvn dependency:tree
```

### Frontend

```bash
# Install dependencies
npm install

# Run development server
npm start

# Build for production
npm run build

# Run tests
npm test

# Analyze bundle size
npm install -g source-map-explorer
source-map-explorer 'build/static/js/*.js'
```

### Database

```bash
# Backup
mysqldump -u root -p ai_virtual_interviewer > backup.sql

# Restore
mysql -u root -p ai_virtual_interviewer < backup.sql

# Check database
mysql -u root -p -e "SELECT * FROM ai_virtual_interviewer.users;"
```

---

## Security Checklist

- [ ] Change JWT secret from default
- [ ] Use strong MySQL password
- [ ] Enable HTTPS in production
- [ ] Set OpenAI API key securely
- [ ] Enable CORS only for trusted domains
- [ ] Implement rate limiting
- [ ] Use environment variables for secrets
- [ ] Enable request validation
- [ ] Implement logging and monitoring
- [ ] Regular security updates

---

This configuration file provides templates for all necessary configurations. Customize based on your specific requirements.

# Setup Instructions

## Prerequisites

Before you begin, ensure you have the following installed:

- **Java Development Kit (JDK)** 17 or higher
- **Node.js** 16 or higher (comes with npm)
- **MySQL Server** 8.0 or higher
- **Maven** 3.8.0 or higher
- **Git** (optional, for cloning)
- **OpenAI API Key** (get from https://platform.openai.com/api-keys)

## Step-by-Step Setup

### 1. Database Setup

#### a. Install MySQL (if not already installed)
- Windows: Download from https://dev.mysql.com/downloads/mysql/
- Mac: `brew install mysql`
- Linux: `sudo apt-get install mysql-server`

#### b. Create Database
```bash
# Open MySQL command line
mysql -u root -p

# Create database
CREATE DATABASE ai_virtual_interviewer;
CREATE USER 'aivi_user'@'localhost' IDENTIFIED BY 'aivi_password';
GRANT ALL PRIVILEGES ON ai_virtual_interviewer.* TO 'aivi_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

#### c. Import Schema (Optional)
```bash
# Run the SQL script to create all tables
mysql -u root -p ai_virtual_interviewer < DATABASE.md
```

### 2. Backend Setup

#### a. Navigate to Backend Directory
```bash
cd AI-Virtual-Interviewer/backend
```

#### b. Configure application.yml
Edit `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ai_virtual_interviewer
    username: aivi_user
    password: aivi_password
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false

jwt:
  secret: your_super_secret_jwt_key_that_is_at_least_32_characters_long
  expiration: 86400000

openai:
  api-key: sk-your-actual-openai-api-key-here
  model: gpt-4
  timeout: 30

server:
  port: 8080
```

#### c. Build Project
```bash
# Clean and build
mvn clean install
```

#### d. Run Backend
```bash
# Option 1: Using Maven
mvn spring-boot:run

# Option 2: Using JAR file
java -jar target/ai-virtual-interviewer-1.0.0.jar
```

Expected output:
```
Started VirtualInterviewerApplication in X.XXX seconds
```

Backend will be available at: `http://localhost:8080`

### 3. Frontend Setup

#### a. Navigate to Frontend Directory
```bash
cd AI-Virtual-Interviewer/frontend
```

#### b. Install Dependencies
```bash
npm install
```

#### c. Create .env File (Optional)
```
REACT_APP_API_URL=http://localhost:8080/api
```

#### d. Start Development Server
```bash
npm start
```

Frontend will automatically open at: `http://localhost:3000`

## Verification

### Test Backend API
```bash
# Test login endpoint
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password"}'

# Get all questions
curl http://localhost:8080/api/questions/public/all
```

### Test Frontend
1. Open http://localhost:3000 in your browser
2. You should see the login page
3. Click on "Register here" to create an account
4. Fill in the form and register
5. You should be redirected to the dashboard

## Environment Configuration

### Backend (application.yml)

```yaml
# JWT Configuration
# Make sure the secret is long and secure
jwt:
  secret: generate-a-secure-random-string-for-production
  expiration: 86400000  # 24 hours in milliseconds

# OpenAI Configuration
# Get your API key from: https://platform.openai.com/api-keys
openai:
  api-key: sk-your-api-key
  model: gpt-4  # or gpt-3.5-turbo for faster/cheaper
  timeout: 30   # seconds

# Database Configuration
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ai_virtual_interviewer
    username: root
    password: your_password
```

### Frontend (if using .env)
```
REACT_APP_API_URL=http://localhost:8080/api
```

## Troubleshooting

### Backend Issues

#### "Connection refused" - MySQL not running
```bash
# Windows
mysql -u root -p

# Mac
brew services start mysql

# Linux
sudo service mysql start
```

#### "Unknown database" error
```bash
# Create database manually
mysql -u root -p -e "CREATE DATABASE ai_virtual_interviewer;"
```

#### Port 8080 already in use
```bash
# Find process using port 8080
netstat -ano | findstr :8080

# Kill the process (Windows)
taskkill /PID <PID> /F
```

#### OpenAI API Key issues
- Verify key format starts with `sk-`
- Check you have API credits: https://platform.openai.com/account/billing/overview
- Ensure key hasn't expired

### Frontend Issues

#### npm install fails
```bash
# Clear npm cache
npm cache clean --force

# Try installing again
npm install
```

#### Port 3000 already in use
```bash
# The app will ask to use a different port
# Or kill the process:

# Windows
netstat -ano | findstr :3000
taskkill /PID <PID> /F

# Mac/Linux
lsof -i :3000
kill -9 <PID>
```

#### "Cannot find module" errors
```bash
# Reinstall dependencies
rm -rf node_modules package-lock.json
npm install
```

#### API calls not working
- Ensure backend is running on port 8080
- Check browser console for CORS errors
- Verify token is being saved in localStorage

## Production Deployment

### Backend Deployment (e.g., Heroku)

1. Create `Procfile`:
```
web: java -Dserver.port=$PORT $JAVA_OPTS -jar target/ai-virtual-interviewer-1.0.0.jar
```

2. Deploy:
```bash
heroku login
heroku create your-app-name
heroku config:set JWT_SECRET=your-secret-key
heroku config:set OPENAI_API_KEY=your-api-key
git push heroku main
```

### Frontend Deployment (e.g., Vercel/Netlify)

1. Build:
```bash
npm run build
```

2. Deploy build folder to Vercel/Netlify

## Testing Credentials

For initial testing, you can use:
```
Email: test@example.com
Password: password123
```

To create this test user, register through the application.

## Performance Tips

1. **Enable Query Caching** in MySQL for better performance
2. **Use Redis** for session caching in production
3. **Enable Gzip Compression** in Spring Boot
4. **Use a CDN** for frontend assets in production
5. **Implement Rate Limiting** for API endpoints

## Security Checklist

- [ ] Change JWT secret to a strong random string
- [ ] Update MySQL credentials to strong passwords
- [ ] Enable HTTPS in production
- [ ] Implement rate limiting
- [ ] Use environment variables for sensitive data
- [ ] Regularly update dependencies
- [ ] Enable CORS only for trusted domains
- [ ] Implement request validation
- [ ] Enable SQL injection protection
- [ ] Use secrets management (AWS Secrets Manager, etc.)

## Support

For issues:
1. Check the troubleshooting section
2. Review the README.md for architecture details
3. Check application logs for errors
4. Review browser console for frontend errors

---

Happy coding! 🚀

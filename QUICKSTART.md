# Quick Start Guide

Get your AI Virtual Interviewer up and running in 15 minutes!

## Prerequisites Check (2 minutes)

```bash
# Check Java
java -version  # Should be Java 17+

# Check Node
node -v  # Should be v16+
npm -v   # Should be v8+

# Check MySQL
mysql --version  # Should be MySQL 8+
```

If any are missing, install them first.

---

## Step 1: Database Setup (3 minutes)

### Open MySQL Command Line
```bash
mysql -u root -p
# Enter your MySQL root password
```

### Create Database and User
```sql
CREATE DATABASE ai_virtual_interviewer;
CREATE USER 'aivi_user'@'localhost' IDENTIFIED BY 'aivi_password';
GRANT ALL PRIVILEGES ON ai_virtual_interviewer.* TO 'aivi_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

✅ **Database created!**

---

## Step 2: Backend Configuration (2 minutes)

### Edit Application Configuration
Navigate to: `backend/src/main/resources/application.yml`

Update these values:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ai_virtual_interviewer
    username: aivi_user          # ← Change if different
    password: aivi_password      # ← Change if different

jwt:
  secret: my_super_secret_key_change_this_to_something_long_and_random

openai:
  api-key: sk-your-actual-openai-api-key-here  # ← IMPORTANT: Get from OpenAI
```

### Get OpenAI API Key
1. Visit: https://platform.openai.com/api-keys
2. Click "Create new secret key"
3. Copy and paste it into application.yml

✅ **Backend configured!**

---

## Step 3: Backend Startup (3 minutes)

### Build Backend
```bash
cd backend
mvn clean install
```

### Run Backend
```bash
mvn spring-boot:run
```

Wait for the message:
```
Started VirtualInterviewerApplication in X seconds
```

✅ **Backend running on http://localhost:8080**

---

## Step 4: Frontend Setup (3 minutes)

### Open New Terminal and Navigate
```bash
cd frontend
npm install
```

### Start Frontend
```bash
npm start
```

The browser should automatically open: http://localhost:3000

✅ **Frontend running on http://localhost:3000**

---

## Step 5: Test the Application (2 minutes)

### In Your Browser (at http://localhost:3000)

1. **Register** - Click "Register here"
   - Email: `test@example.com`
   - Password: `password123`
   - First Name: `John`
   - Last Name: `Doe`
   - Target Role: `Software Engineer`
   - Click "Register"

2. **Verify Login** - You should be redirected to Dashboard

3. **Start Interview** - Click "Start New Interview"
   - Job Role: `Software Engineer`
   - Domain: `DSA`
   - Number of Questions: `2`
   - Click "Start Interview"

4. **Answer Question** - You should see a question
   - Type an answer
   - Click "Submit Answer"

✅ **Application is working!**

---

## Troubleshooting Quick Fixes

### Backend Won't Start
```bash
# Check if port 8080 is in use
# Windows
netstat -ano | findstr :8080

# Kill the process if needed
taskkill /PID <PID> /F

# Try again
mvn spring-boot:run
```

### Database Connection Error
```bash
# Verify MySQL is running
# Windows
mysql -u aivi_user -p aivi_password

# Mac
brew services start mysql

# If still failing, restart MySQL
```

### Frontend Port 3000 Conflict
```bash
# The app will ask to use a different port
# Or run on different port:
BROWSER=none npm start
# Then open http://localhost:3001 (or suggested port)
```

### NPM Install Fails
```bash
npm cache clean --force
rm -rf node_modules package-lock.json
npm install
```

### "Cannot connect to backend"
- Check backend is running: http://localhost:8080/api/questions/public/all
- Check browser console for CORS errors
- Ensure both frontend and backend terminals are running

---

## File Structure You Created

```
AI-Virtual-Interviewer/
├── backend/               ← Java Spring Boot
│   ├── src/main/java/    ← All Java code
│   ├── pom.xml           ← Maven config
│   └── target/           ← Will be created
├── frontend/              ← React App
│   ├── src/              ← React code
│   ├── package.json      ← npm config
│   └── node_modules/     ← Will be created
├── README.md             ← Full documentation
├── SETUP.md              ← Detailed setup guide
├── DATABASE.md           ← SQL scripts
├── API_DOCUMENTATION.md  ← API reference
└── CONFIGURATION.md      ← Config templates
```

---

## Default Test Credentials

After registering, you can login with:
- **Email**: test@example.com
- **Password**: password123

---

## What Each Terminal Does

### Terminal 1 (Backend)
```bash
cd backend
mvn spring-boot:run
# Runs Spring Boot backend on port 8080
# Handles all business logic and AI
# Keep this running!
```

### Terminal 2 (Frontend)
```bash
cd frontend
npm start
# Runs React frontend on port 3000
# Opens your browser automatically
# Keep this running!
```

Both terminals must be running for the app to work!

---

## Next Steps

1. **Explore Features**
   - Try starting an interview
   - Check analytics
   - Create questions (Admin)

2. **Customize**
   - Add your own interview questions
   - Modify colors in `App.css`
   - Change content in pages

3. **Deploy** (when ready)
   - Backend: Heroku, AWS, Google Cloud
   - Frontend: Vercel, Netlify
   - Database: AWS RDS, Google Cloud SQL
   - See SETUP.md for deployment guide

4. **Learn & Improve**
   - Study the code structure
   - Read API documentation
   - Try modifying features
   - Add new features

---

## File Locations for Quick Reference

| What | Location |
|------|----------|
| Backend Config | `backend/src/main/resources/application.yml` |
| Frontend Env | `frontend/.env` (create if needed) |
| API Service | `frontend/src/services/apiService.js` |
| Backend Main | `backend/src/main/java/com/virtualinterviewer/` |
| Frontend Pages | `frontend/src/pages/` |
| Database Setup | `DATABASE.md` |
| Full Guide | `SETUP.md` |

---

## Common API Tests

### Test Backend is Running
```bash
curl http://localhost:8080/api/questions/public/all
```

### Test Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'
```

### Test with Frontend
Just use the browser interface - all API calls are integrated!

---

## Success Indicators

✅ Backend starts without errors
✅ Frontend opens in browser automatically
✅ Can register and login
✅ Can see dashboard with question stats
✅ Can start an interview
✅ Can answer questions and submit
✅ Can see analytics page
✅ Admin can create questions (if role is set to ADMIN)

---

## Important Notes

1. **Both servers must be running** - Backend and Frontend
2. **OpenAI API key is required** - Add to `application.yml`
3. **MySQL must be running** - Database connection needed
4. **First load might be slow** - AI initialization takes time
5. **Check terminal output** - Errors appear in terminals, not just browser

---

## Getting Help

1. **Check SETUP.md** - Detailed troubleshooting
2. **Check DATABASE.md** - SQL and schema issues
3. **Check API_DOCUMENTATION.md** - API endpoint details
4. **Read error messages** - They usually tell you what's wrong
5. **Check browser console** - F12 in browser for frontend errors
6. **Check terminal output** - Backend errors in terminal

---

## Terminal Output Checklist

### Backend Should Show:
```
o.s.b.w.e.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080
Started VirtualInterviewerApplication in X seconds
```

### Frontend Should Show:
```
Compiled successfully!
You can now view ai-virtual-interviewer in the browser.
  Local: http://localhost:3000
```

---

## Time Estimates

| Step | Time |
|------|------|
| Prerequisites | 2 min |
| Database Setup | 3 min |
| Backend Config | 2 min |
| Backend Start | 3 min |
| Frontend Setup | 3 min |
| Testing | 2 min |
| **Total** | **~15 min** |

---

## You're All Set! 🎉

Your AI Virtual Interviewer is now running!

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080/api
- **Database**: ai_virtual_interviewer

Start with the landing page, register, and begin your mock interviews!

For more details, see the comprehensive documentation files.

**Good luck with your college project!** 🚀

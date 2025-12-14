# AI Virtual Interviewer

A comprehensive, AI-powered virtual interviewer platform designed for college students to prepare for placement interviews with full backend and frontend implementation.

## 🎯 Features

### User Features
- **User Authentication**: Secure JWT-based authentication with registration and login
- **Mock Interviews**: AI-powered mock interviews with real-time evaluation
- **Multiple Interview Types**: 
  - Technical (DSA, System Design, etc.)
  - Behavioral/HR
  - Domain-specific questions
- **AI Answer Evaluation**: GPT-4 powered real-time answer analysis and scoring
- **Voice Interview Support**: 
  - Speech-to-text answer input
  - Text-to-speech interview questions
- **Performance Analytics**: Detailed dashboard showing:
  - Interview history
  - Score trends
  - Strengths and weaknesses
  - Overall progress tracking
- **Timer-based Interviews**: Configurable time limits for questions
- **Resume Analysis**: AI considers candidate's resume for personalized questions

### Admin Features
- **Question Management**: Create, edit, delete interview questions
- **Question Categories**: Organize by domain, job role, and difficulty level
- **Quality Control**: Manage question pools across multiple categories

## 📁 Project Structure

```
AI-Virtual-Interviewer/
├── backend/
│   ├── src/main/java/com/virtualinterviewer/
│   │   ├── config/           # Security & application config
│   │   ├── controller/       # REST API endpoints
│   │   ├── model/            # JPA entities
│   │   ├── repository/       # Data access layer
│   │   ├── service/          # Business logic
│   │   ├── security/         # JWT & authentication
│   │   ├── dto/              # Data transfer objects
│   │   └── util/             # Utility classes
│   ├── src/main/resources/
│   │   └── application.yml   # Spring configuration
│   └── pom.xml              # Maven dependencies
└── frontend/
    ├── src/
    │   ├── pages/           # Page components
    │   ├── components/      # Reusable components
    │   ├── services/        # API service layer
    │   ├── atom/            # Jotai state management
    │   ├── styles/          # CSS styling
    │   ├── App.js           # Main app component
    │   └── index.js         # Entry point
    ├── public/
    │   └── index.html       # HTML template
    └── package.json         # NPM dependencies
```

## 🚀 Getting Started

### Prerequisites
- **Java 17+** (for backend)
- **Node.js 16+** (for frontend)
- **MySQL 8.0+** (for database)
- **OpenAI API Key** (for AI features)

### Backend Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd AI-Virtual-Interviewer/backend
   ```

2. **Configure MySQL Database**
   ```sql
   CREATE DATABASE ai_virtual_interviewer;
   ```

3. **Update application.yml**
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/ai_virtual_interviewer
       username: root
       password: your_password
   
   jwt:
     secret: your_super_secret_jwt_key
   
   openai:
     api-key: your_openai_api_key
   ```

4. **Build and Run Backend**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```
   Backend will be available at `http://localhost:8080`

### Frontend Setup

1. **Navigate to frontend directory**
   ```bash
   cd ../frontend
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Start development server**
   ```bash
   npm start
   ```
   Frontend will be available at `http://localhost:3000`

## 🔑 API Endpoints

### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login

### Interviews
- `POST /api/interviews/start` - Start new interview
- `GET /api/interviews/:id` - Get interview details
- `GET /api/interviews/:id/next-question` - Get next question
- `POST /api/interviews/:id/submit-answer` - Submit answer
- `POST /api/interviews/:id/complete` - Complete interview
- `GET /api/interviews/my-interviews` - Get user's interviews

### Questions
- `GET /api/questions/public/all` - Get all questions
- `GET /api/questions/public/domain/:domain/role/:jobRole` - Get filtered questions
- `GET /api/questions/public/type/:type/domain/:domain` - Get by type
- `GET /api/questions/public/difficulty/:domain/:difficulty` - Get by difficulty

### Analytics
- `GET /api/analytics/my-analytics` - Get user analytics

### Admin
- `POST /api/admin/questions/create` - Create question
- `PUT /api/admin/questions/update/:id` - Update question
- `DELETE /api/admin/questions/delete/:id` - Delete question

## 📊 Database Schema

### Tables
1. **users** - User accounts and profiles
2. **interview_questions** - Question bank
3. **interviews** - Interview sessions
4. **answers** - User answers to questions
5. **feedback** - Interview feedback
6. **analytics** - User performance analytics

## 🔐 Security Features

- JWT-based authentication
- Password encryption (BCrypt)
- CORS configuration
- Role-based access control (ADMIN, USER)
- Secure API endpoints

## 🤖 AI Integration

### OpenAI GPT-4 Features
1. **Question Generation**
   - Domain-specific questions
   - Difficulty level variation
   - Resume-based personalization

2. **Answer Evaluation**
   - Real-time scoring
   - Detailed feedback
   - Strength and weakness analysis

3. **Interview Feedback**
   - Comprehensive performance review
   - Improvement suggestions
   - Comparative analysis

## 🎨 Frontend Technologies

- **React 18** - UI framework
- **React Router** - Navigation
- **Bootstrap 5** - Styling
- **Jotai** - State management
- **Axios** - HTTP client
- **Chart.js** - Analytics visualization
- **React Icons** - Icon library

## 🛠️ Backend Technologies

- **Spring Boot 3.2** - Framework
- **Spring Security** - Authentication
- **Spring Data JPA** - ORM
- **JWT** - Token-based auth
- **OpenAI Java SDK** - AI integration
- **MySQL** - Database
- **Maven** - Build tool

## 📝 Sample Usage Flow

1. **User Registration/Login**
   - Create account with email and password
   - Set target job role

2. **Start Interview**
   - Select job role and domain
   - Choose number of questions
   - Optional: Upload resume

3. **Answer Questions**
   - Read AI-generated questions
   - Answer via text or voice
   - Get real-time AI feedback
   - Move to next question

4. **View Results**
   - See overall score
   - Read detailed feedback
   - Track performance metrics

5. **Analytics**
   - Monitor progress over time
   - Identify strengths/weaknesses
   - Plan improvement areas

## 🔄 API Integration Example

```javascript
// Start interview
const response = await interviewService.startInterview({
  jobRole: 'Software Engineer',
  domain: 'DSA',
  numberOfQuestions: 5,
  resumeContent: 'Your resume text'
});

// Get next question
const question = await interviewService.getNextQuestion(interviewId);

// Submit answer
await interviewService.submitAnswer(interviewId, {
  questionId: question.id,
  answerText: 'User answer',
  timeTakenSeconds: 120
});
```

## 🐛 Troubleshooting

### Backend Issues
- Ensure MySQL is running
- Check database credentials
- Verify OpenAI API key is valid
- Check port 8080 is available

### Frontend Issues
- Clear node_modules and reinstall: `npm ci`
- Clear browser cache
- Ensure backend is running on port 8080
- Check CORS configuration

## 📱 Future Enhancements

- [ ] Mobile app version
- [ ] Video interview recording
- [ ] Peer practice sessions
- [ ] Gamification features
- [ ] Company-specific question sets
- [ ] Live interview practice with mentors
- [ ] Resume review and optimization
- [ ] Interview scheduling
- [ ] Advanced analytics and predictions

## 📄 Configuration Files

### application.yml
```yaml
spring:
  application:
    name: ai-virtual-interviewer
  datasource:
    url: jdbc:mysql://localhost:3306/ai_virtual_interviewer
    username: root
    password: password
  jpa:
    hibernate:
      ddl-auto: update

jwt:
  secret: your_secret_key_here
  expiration: 86400000

openai:
  api-key: your_openai_key
  model: gpt-4
```

## 🤝 Contributing

To contribute to this project:
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

## 📄 License

This project is open source and available under the MIT License.

## 📞 Support

For issues or questions:
- Open an issue on GitHub
- Check existing documentation
- Review API documentation

---

**Happy Interviewing!** 🎯

Built with ❤️ for placement preparation

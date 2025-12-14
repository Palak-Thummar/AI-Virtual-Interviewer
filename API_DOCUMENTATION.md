# API Documentation

## Authentication Endpoints

### Register User
```
POST /api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "9876543210",
  "targetRole": "Software Engineer"
}

Response (200 OK):
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "id": 1,
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "role": "USER"
}
```

### Login User
```
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}

Response (200 OK):
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "id": 1,
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "role": "USER"
}
```

## Interview Endpoints

### Start Interview
```
POST /api/interviews/start
Authorization: Bearer <token>
Content-Type: application/json

{
  "jobRole": "Software Engineer",
  "domain": "DSA",
  "numberOfQuestions": 5,
  "resumeContent": "Your resume text here"
}

Response (200 OK):
{
  "id": 1,
  "user": {...},
  "jobRole": "Software Engineer",
  "domain": "DSA",
  "status": "IN_PROGRESS",
  "startTime": "2024-01-01T10:00:00",
  "totalQuestions": 5,
  "questionsAnswered": 0,
  "overallScore": 0
}
```

### Get Interview
```
GET /api/interviews/{interviewId}
Authorization: Bearer <token>

Response (200 OK):
{
  "id": 1,
  "jobRole": "Software Engineer",
  "domain": "DSA",
  "status": "IN_PROGRESS",
  "totalQuestions": 5,
  "questionsAnswered": 2,
  "overallScore": 75.5
}
```

### Get Next Question
```
GET /api/interviews/{interviewId}/next-question
Authorization: Bearer <token>

Response (200 OK):
{
  "id": 1,
  "question": "What is the time complexity of Binary Search?",
  "type": "TECHNICAL",
  "domain": "DSA",
  "jobRole": "Software Engineer",
  "difficulty": 2,
  "timeLimitSeconds": 60,
  "hints": "Think about how many times you can divide n by 2"
}
```

### Submit Answer
```
POST /api/interviews/{interviewId}/submit-answer
Authorization: Bearer <token>
Content-Type: application/json

{
  "questionId": 1,
  "answerText": "Binary search has O(log n) time complexity",
  "answerAudio": "audio_file_path",
  "timeTakenSeconds": 45
}

Response (200 OK):
{
  "id": 1,
  "interview": {...},
  "question": {...},
  "answerText": "Binary search has O(log n) time complexity",
  "score": 85,
  "aiEvaluation": "Correct! Your answer demonstrates...",
  "answeredAt": "2024-01-01T10:02:00"
}
```

### Complete Interview
```
POST /api/interviews/{interviewId}/complete
Authorization: Bearer <token>

Response (200 OK):
{
  "id": 1,
  "status": "COMPLETED",
  "endTime": "2024-01-01T10:30:00",
  "questionsAnswered": 5,
  "overallScore": 82.3
}
```

### Get My Interviews
```
GET /api/interviews/my-interviews
Authorization: Bearer <token>

Response (200 OK):
[
  {
    "id": 1,
    "jobRole": "Software Engineer",
    "domain": "DSA",
    "status": "COMPLETED",
    "overallScore": 82.3
  },
  {
    "id": 2,
    "jobRole": "Frontend Developer",
    "domain": "System Design",
    "status": "IN_PROGRESS",
    "overallScore": 0
  }
]
```

## Question Endpoints

### Get All Questions
```
GET /api/questions/public/all

Response (200 OK):
[
  {
    "id": 1,
    "question": "What is Binary Search?",
    "type": "TECHNICAL",
    "domain": "DSA",
    "difficulty": 2,
    "timeLimitSeconds": 60
  },
  ...
]
```

### Get Questions by Domain and Role
```
GET /api/questions/public/domain/{domain}/role/{jobRole}

Example: GET /api/questions/public/domain/DSA/role/Software%20Engineer

Response (200 OK):
[
  {...question objects...}
]
```

### Get Questions by Type and Domain
```
GET /api/questions/public/type/{type}/domain/{domain}

Example: GET /api/questions/public/type/TECHNICAL/domain/DSA

Response (200 OK):
[
  {...question objects...}
]
```

### Get Question by ID
```
GET /api/questions/public/{id}

Response (200 OK):
{
  "id": 1,
  "question": "What is Binary Search?",
  "type": "TECHNICAL",
  "domain": "DSA",
  "expectedAnswer": "Binary search is...",
  "hints": "Think about divided and conquer",
  "difficulty": 2,
  "timeLimitSeconds": 60
}
```

### Get Questions by Difficulty
```
GET /api/questions/public/difficulty/{domain}/{difficulty}

Example: GET /api/questions/public/difficulty/DSA/3

Response (200 OK):
[
  {...question objects with difficulty 3...}
]
```

## Analytics Endpoints

### Get My Analytics
```
GET /api/analytics/my-analytics
Authorization: Bearer <token>

Response (200 OK):
{
  "id": 1,
  "user": {...},
  "totalInterviews": 5,
  "completedInterviews": 5,
  "averageScore": 78.5,
  "bestScore": 92.0,
  "worstScore": 65.0,
  "topicStrengths": "DSA, Problem Solving",
  "topicWeaknesses": "System Design, Database",
  "lastInterviewDate": "2024-01-01T10:30:00",
  "lastUpdated": "2024-01-01T10:30:00"
}
```

## Admin Endpoints

### Create Question
```
POST /api/admin/questions/create
Authorization: Bearer <admin_token>
Content-Type: application/json

{
  "question": "What is a binary tree?",
  "type": "TECHNICAL",
  "domain": "DSA",
  "jobRole": "Software Engineer",
  "expectedAnswer": "A binary tree is a tree data structure...",
  "hints": "Think about nodes with at most 2 children",
  "difficulty": 3,
  "timeLimitSeconds": 120,
  "isActive": true,
  "createdBy": "admin@example.com"
}

Response (200 OK):
{
  "id": 101,
  "question": "What is a binary tree?",
  ...
}
```

### Update Question
```
PUT /api/admin/questions/update/{id}
Authorization: Bearer <admin_token>
Content-Type: application/json

{
  "question": "What is a binary tree? (Updated)",
  ...
}

Response (200 OK):
{
  "id": 101,
  "question": "What is a binary tree? (Updated)",
  ...
}
```

### Delete Question
```
DELETE /api/admin/questions/delete/{id}
Authorization: Bearer <admin_token>

Response (200 OK):
{
  "message": "Question deleted successfully"
}
```

## Error Responses

### 400 Bad Request
```json
{
  "error": "Error: User already exists with this email"
}
```

### 401 Unauthorized
```json
{
  "error": "Invalid token or token expired"
}
```

### 403 Forbidden
```json
{
  "error": "You don't have permission to access this resource"
}
```

### 404 Not Found
```json
{
  "error": "Interview not found"
}
```

### 500 Internal Server Error
```json
{
  "error": "An unexpected error occurred"
}
```

## Request Headers

All authenticated requests must include:
```
Authorization: Bearer <your_jwt_token>
Content-Type: application/json
```

## Response Codes

- `200 OK` - Successful request
- `201 Created` - Resource created successfully
- `400 Bad Request` - Invalid request data
- `401 Unauthorized` - Missing or invalid authentication
- `403 Forbidden` - Access denied
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

---

For more details, check the README.md and SETUP.md files.

# Database Schema

## SQL Script for AI Virtual Interviewer

```sql
-- Create Database
CREATE DATABASE IF NOT EXISTS ai_virtual_interviewer;
USE ai_virtual_interviewer;

-- Users Table
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(15),
    target_role VARCHAR(500),
    resume_path TEXT,
    bio TEXT,
    role ENUM('USER', 'ADMIN') DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_role (role)
);

-- Interview Questions Table
CREATE TABLE interview_questions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    question TEXT NOT NULL,
    type ENUM('TECHNICAL', 'BEHAVIORAL', 'CODING') NOT NULL,
    domain VARCHAR(100) NOT NULL,
    job_role VARCHAR(100) NOT NULL,
    expected_answer TEXT,
    hints TEXT,
    difficulty INT,
    time_limit_seconds INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    created_by VARCHAR(255) NOT NULL,
    INDEX idx_domain_role (domain, job_role),
    INDEX idx_type_domain (type, domain),
    INDEX idx_difficulty (difficulty),
    INDEX idx_active (is_active)
);

-- Interviews Table
CREATE TABLE interviews (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    job_role VARCHAR(255) NOT NULL,
    domain VARCHAR(255) NOT NULL,
    status ENUM('IN_PROGRESS', 'COMPLETED', 'PAUSED', 'ABANDONED') DEFAULT 'IN_PROGRESS',
    start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    end_time TIMESTAMP,
    total_questions INT,
    questions_answered INT DEFAULT 0,
    overall_score DOUBLE DEFAULT 0,
    resume_context_used LONGTEXT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_start_time (start_time)
);

-- Answers Table
CREATE TABLE answers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    interview_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    answer_text LONGTEXT,
    answer_audio VARCHAR(500),
    time_taken_seconds INT,
    answered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    score DOUBLE,
    ai_evaluation TEXT,
    is_correct BOOLEAN,
    FOREIGN KEY (interview_id) REFERENCES interviews(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES interview_questions(id) ON DELETE CASCADE,
    INDEX idx_interview_id (interview_id),
    INDEX idx_question_id (question_id),
    INDEX idx_answered_at (answered_at)
);

-- Feedback Table
CREATE TABLE feedback (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    interview_id BIGINT NOT NULL,
    strengths TEXT,
    weaknesses TEXT,
    improvements TEXT,
    overall_comments TEXT,
    overall_score DOUBLE,
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (interview_id) REFERENCES interviews(id) ON DELETE CASCADE,
    INDEX idx_interview_id (interview_id),
    INDEX idx_generated_at (generated_at)
);

-- Analytics Table
CREATE TABLE analytics (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT UNIQUE NOT NULL,
    total_interviews INT DEFAULT 0,
    completed_interviews INT DEFAULT 0,
    average_score DOUBLE DEFAULT 0,
    best_score DOUBLE DEFAULT 0,
    worst_score DOUBLE DEFAULT 0,
    topic_strengths JSON,
    topic_weaknesses JSON,
    total_time_spent INT DEFAULT 0,
    last_interview_date TIMESTAMP,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id)
);

-- Indexes for Performance
CREATE INDEX idx_interview_user_date ON interviews(user_id, start_time);
CREATE INDEX idx_answer_interview_question ON answers(interview_id, question_id);
CREATE INDEX idx_feedback_interview ON feedback(interview_id);

-- Sample Data (Optional)
-- Insert sample questions
INSERT INTO interview_questions (question, type, domain, job_role, expected_answer, difficulty, time_limit_seconds, is_active, created_by)
VALUES 
('What is the time complexity of Binary Search?', 'TECHNICAL', 'DSA', 'Software Engineer', 'O(log n)', 2, 60, TRUE, 'admin'),
('How would you design a URL shortening service?', 'TECHNICAL', 'System Design', 'Software Engineer', 'Use hash tables, distributed caching, database...', 4, 300, TRUE, 'admin'),
('Tell us about a time you worked in a team.', 'BEHAVIORAL', 'HR', 'Software Engineer', 'Share a specific example...', 1, 120, TRUE, 'admin');
```

## Table Descriptions

### users
Stores user information and authentication details.
- `id`: Primary key
- `email`: Unique email address
- `password`: Encrypted password
- `role`: User role (USER or ADMIN)

### interview_questions
Contains all available interview questions.
- `id`: Primary key
- `question`: The actual question text
- `type`: Question type (TECHNICAL, BEHAVIORAL, CODING)
- `domain`: Domain of the question
- `difficulty`: 1-5 scale
- `is_active`: Whether question is available

### interviews
Tracks interview sessions.
- `id`: Primary key
- `user_id`: Reference to user
- `status`: Current status of interview
- `overall_score`: Final score out of 100

### answers
Stores user answers for each question.
- `interview_id`: Reference to interview
- `question_id`: Reference to question
- `answer_text`: User's answer
- `score`: AI-given score
- `ai_evaluation`: Detailed feedback

### feedback
Interview feedback and analysis.
- `interview_id`: Reference to interview
- `strengths`: Areas where user performed well
- `weaknesses`: Areas for improvement
- `overall_score`: Final evaluation

### analytics
User performance metrics and statistics.
- `user_id`: Reference to user
- `total_interviews`: Count of all interviews
- `average_score`: Average performance
- `best_score`: Highest score
- `worst_score`: Lowest score

import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add token to every request
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Auth Services
export const authService = {
  register: (data) => apiClient.post('/auth/register', data),
  login: (data) => apiClient.post('/auth/login', data),
};

// Interview Services
export const interviewService = {
  startInterview: (data) => apiClient.post('/interviews/start', data),
  getInterview: (id) => apiClient.get(`/interviews/${id}`),
  getNextQuestion: (interviewId) => apiClient.get(`/interviews/${interviewId}/next-question`),
  submitAnswer: (interviewId, data) => apiClient.post(`/interviews/${interviewId}/submit-answer`, data),
  completeInterview: (interviewId) => apiClient.post(`/interviews/${interviewId}/complete`),
  getMyInterviews: () => apiClient.get('/interviews/my-interviews'),
};

// Question Services
export const questionService = {
  getAllQuestions: () => apiClient.get('/questions/public/all'),
  getQuestionsByDomainAndRole: (domain, jobRole) =>
    apiClient.get(`/questions/public/domain/${domain}/role/${jobRole}`),
  getQuestionsByType: (type, domain) => apiClient.get(`/questions/public/type/${type}/domain/${domain}`),
  getQuestionById: (id) => apiClient.get(`/questions/public/${id}`),
  getQuestionsByDifficulty: (domain, difficulty) =>
    apiClient.get(`/questions/public/difficulty/${domain}/${difficulty}`),
};

// Analytics Services
export const analyticsService = {
  getMyAnalytics: () => apiClient.get('/analytics/my-analytics'),
};

// Admin Services
export const adminService = {
  createQuestion: (data) => apiClient.post('/admin/questions/create', data),
  updateQuestion: (id, data) => apiClient.put(`/admin/questions/update/${id}`, data),
  deleteQuestion: (id) => apiClient.delete(`/admin/questions/delete/${id}`),
};

export default apiClient;

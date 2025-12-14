import './App.css';
import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { useAtom } from 'jotai';
import { authAtom } from './atom/atoms';

// Pages
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import DashboardPage from './pages/DashboardPage';
import InterviewPage from './pages/InterviewPage';
import ResultsPage from './pages/ResultsPage';
import AnalyticsPage from './pages/AnalyticsPage';
import AdminPage from './pages/AdminPage';

// Components
import Navbar from './components/Navbar';

function App() {
  const [auth] = useAtom(authAtom);

  React.useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) {
      // Token exists, consider user as logged in
    }
  }, []);

  const isAuthenticated = localStorage.getItem('token');

  return (
    <Router>
      {isAuthenticated && <Navbar />}
      <div className="app-container">
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route
            path="/dashboard"
            element={isAuthenticated ? <DashboardPage /> : <Navigate to="/login" />}
          />
          <Route
            path="/interview/:interviewId"
            element={isAuthenticated ? <InterviewPage /> : <Navigate to="/login" />}
          />
          <Route
            path="/results/:interviewId"
            element={isAuthenticated ? <ResultsPage /> : <Navigate to="/login" />}
          />
          <Route
            path="/analytics"
            element={isAuthenticated ? <AnalyticsPage /> : <Navigate to="/login" />}
          />
          <Route
            path="/admin"
            element={isAuthenticated ? <AdminPage /> : <Navigate to="/login" />}
          />
          <Route path="/" element={<Navigate to={isAuthenticated ? "/dashboard" : "/login"} />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;

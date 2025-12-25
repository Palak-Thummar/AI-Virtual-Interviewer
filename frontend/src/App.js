import './App.css';
import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from './hooks/useAuth';

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

const PrivateRoute = ({ children }) => {
  const { isAuthenticated } = useAuth();
  return isAuthenticated ? children : <Navigate to="/login" />;
};

const PublicRoute = ({ children }) => {
  const { isAuthenticated } = useAuth();
  return isAuthenticated ? <Navigate to="/dashboard" /> : children;
};

function App() {
  const { isAuthenticated } = useAuth();

  return (
    <>
      {isAuthenticated && <Navbar />}
      <div className="app-container">
        <Routes>
          <Route 
            path="/login" 
            element={<PublicRoute><LoginPage /></PublicRoute>} 
          />
          <Route 
            path="/register" 
            element={<PublicRoute><RegisterPage /></PublicRoute>} 
          />
          <Route
            path="/dashboard"
            element={<PrivateRoute><DashboardPage /></PrivateRoute>}
          />
          <Route
            path="/interview/:interviewId"
            element={<PrivateRoute><InterviewPage /></PrivateRoute>}
          />
          <Route
            path="/results/:interviewId"
            element={<PrivateRoute><ResultsPage /></PrivateRoute>}
          />
          <Route
            path="/analytics"
            element={<PrivateRoute><AnalyticsPage /></PrivateRoute>}
          />
          <Route
            path="/admin"
            element={<PrivateRoute><AdminPage /></PrivateRoute>}
          />
          <Route path="/" element={<Navigate to={isAuthenticated ? "/dashboard" : "/login"} />} />
        </Routes>
      </div>
    </>
  );
}

export default App;

import React, { useEffect, useState } from 'react';
import { Container, Card, Row, Col, Alert, Table, Badge } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { analyticsService } from '../services/apiService';
import { ToastContainer, toast } from 'react-toastify';
import { Line, Bar } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  BarElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js';
import Loader from '../components/Loader';

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, BarElement, Title, Tooltip, Legend);

function AnalyticsPage() {
  const [analytics, setAnalytics] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const fetchAnalytics = async () => {
      setLoading(true);
      try {
        const response = await analyticsService.getMyAnalytics();
        setAnalytics(response.data);
        setError('');
      } catch (err) {
        console.error('Failed to fetch analytics:', err);
        setError('Failed to load analytics data. Your session might be invalid. Please try logging out and logging in again.');
        toast.error('Failed to load analytics data. Your session might be invalid.');
      } finally {
        setLoading(false);
      }
    };

    fetchAnalytics();
  }, []);

  if (loading) {
    return <Loader />;
  }

  if (error) {
    return <div className="container mt-5"><div className="alert alert-danger">{error}</div></div>;
  }

  if (!analytics) {
    return <div className="container mt-5">No analytics data available.</div>;
  }

  // Prepare chart data from interview history
  const interviewHistory = analytics.interviewHistory || [];
  const chartData = {
    labels: interviewHistory.length > 0 
      ? interviewHistory.map((interview, idx) => `Interview ${idx + 1}`)
      : ['No Data'],
    datasets: [
      {
        label: 'Score',
        data: interviewHistory.length > 0 
          ? interviewHistory.map(interview => interview.score)
          : [0],
        borderColor: '#667eea',
        backgroundColor: 'rgba(102, 126, 234, 0.1)',
        borderWidth: 2,
        tension: 0.4,
      },
    ],
  };

  const chartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: true,
        position: 'top',
      },
      tooltip: {
        callbacks: {
          label: function(context) {
            return `Score: ${context.parsed.y.toFixed(1)}/100`;
          }
        }
      }
    },
    scales: {
      y: {
        beginAtZero: true,
        max: 100,
        ticks: {
          callback: function(value) {
            return value + '%';
          }
        }
      }
    }
  };

  // Prepare domain performance chart
  const domainPerformance = analytics.domainPerformance || {};
  const domainChartData = {
    labels: Object.keys(domainPerformance).length > 0 
      ? Object.keys(domainPerformance)
      : ['No Data'],
    datasets: [
      {
        label: 'Average Score by Domain',
        data: Object.keys(domainPerformance).length > 0
          ? Object.values(domainPerformance)
          : [0],
        backgroundColor: [
          'rgba(102, 126, 234, 0.7)',
          'rgba(118, 75, 162, 0.7)',
          'rgba(237, 100, 166, 0.7)',
          'rgba(255, 154, 158, 0.7)',
        ],
        borderColor: [
          '#667eea',
          '#764ba2',
          '#ed64a6',
          '#ff9a9e',
        ],
        borderWidth: 2,
      },
    ],
  };

  const domainChartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: false,
      },
    },
    scales: {
      y: {
        beginAtZero: true,
        max: 100,
      }
    }
  };

  return (
    <Container className="py-5">
      <ToastContainer position="top-right" autoClose={3000} />

      <div className="mb-4">
        <h1>Your Analytics Dashboard</h1>
        <p className="text-muted">Track your interview preparation progress</p>
      </div>

      {/* Stats Cards */}
      <Row className="mb-4">
        <Col md={3} className="mb-3">
          <Card className="card-custom text-center">
            <Card.Body>
              <Card.Title>Total Interviews</Card.Title>
              <div style={{ fontSize: '48px', fontWeight: 'bold', color: '#667eea' }}>
                {analytics.totalInterviews || 0}
              </div>
            </Card.Body>
          </Card>
        </Col>

        <Col md={3} className="mb-3">
          <Card className="card-custom text-center">
            <Card.Body>
              <Card.Title>Completed</Card.Title>
              <div style={{ fontSize: '48px', fontWeight: 'bold', color: '#28a745' }}>
                {analytics.completedInterviews || 0}
              </div>
            </Card.Body>
          </Card>
        </Col>

        <Col md={3} className="mb-3">
          <Card className="card-custom text-center">
            <Card.Body>
              <Card.Title>Average Score</Card.Title>
              <div style={{ fontSize: '48px', fontWeight: 'bold', color: '#ffc107' }}>
                {analytics.averageScore ? analytics.averageScore.toFixed(1) : '0.0'}
              </div>
              <small className="text-muted">out of 100</small>
            </Card.Body>
          </Card>
        </Col>

        <Col md={3} className="mb-3">
          <Card className="card-custom text-center">
            <Card.Body>
              <Card.Title>Best Score</Card.Title>
              <div style={{ fontSize: '48px', fontWeight: 'bold', color: '#17a2b8' }}>
                {analytics.bestScore ? analytics.bestScore.toFixed(1) : '0.0'}
              </div>
              <small className="text-muted">personal best</small>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Charts */}
      <Row className="mb-4">
        <Col md={8} className="mb-4">
          <Card className="card-custom">
            <Card.Body>
              <Card.Title>Score Trend Over Time</Card.Title>
              <div style={{ height: '300px' }}>
                {interviewHistory.length > 0 ? (
                  <Line data={chartData} options={chartOptions} />
                ) : (
                  <Alert variant="info">Complete interviews to see your score trend</Alert>
                )}
              </div>
            </Card.Body>
          </Card>
        </Col>

        <Col md={4} className="mb-4">
          <Card className="card-custom">
            <Card.Body>
              <Card.Title>Domain Performance</Card.Title>
              <div style={{ height: '300px' }}>
                {Object.keys(domainPerformance).length > 0 ? (
                  <Bar data={domainChartData} options={domainChartOptions} />
                ) : (
                  <Alert variant="info">Complete interviews in different domains to see performance</Alert>
                )}
              </div>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Interview History Table */}
      <Row className="mb-4">
        <Col md={12}>
          <Card className="card-custom">
            <Card.Body>
              <Card.Title>Recent Interview History</Card.Title>
              {interviewHistory.length > 0 ? (
                <Table striped bordered hover responsive>
                  <thead>
                    <tr>
                      <th>#</th>
                      <th>Job Role</th>
                      <th>Domain</th>
                      <th>Score</th>
                      <th>Date</th>
                      <th>Status</th>
                    </tr>
                  </thead>
                  <tbody>
                    {interviewHistory.map((interview, idx) => (
                      <tr key={interview.id}>
                        <td>{idx + 1}</td>
                        <td>{interview.jobRole}</td>
                        <td>{interview.domain}</td>
                        <td>
                          <strong style={{ color: interview.score >= 70 ? '#28a745' : interview.score >= 50 ? '#ffc107' : '#dc3545' }}>
                            {interview.score.toFixed(1)}%
                          </strong>
                        </td>
                        <td>{new Date(interview.date).toLocaleDateString()}</td>
                        <td>
                          <Badge bg={interview.score >= 70 ? 'success' : interview.score >= 50 ? 'warning' : 'danger'}>
                            {interview.score >= 70 ? 'Excellent' : interview.score >= 50 ? 'Good' : 'Needs Improvement'}
                          </Badge>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </Table>
              ) : (
                <Alert variant="info">No interview history available yet. Complete your first interview to see data here.</Alert>
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Strengths and Weaknesses */}
      <Row>
        <Col md={6} className="mb-4">
          <Card className="card-custom">
            <Card.Body>
              <Card.Title>✅ Strengths</Card.Title>
              {analytics.topicStrengths ? (
                <ul className="list-unstyled">
                  {analytics.topicStrengths.split(',').map((item, idx) => (
                    <li key={idx} className="mb-2 p-2" style={{ backgroundColor: '#d4edda', borderRadius: '5px' }}>
                      ✓ {item.trim()}
                    </li>
                  ))}
                </ul>
              ) : (
                <Alert variant="info">
                  Complete more interviews to identify your strengths. The system will analyze your performance across different topics.
                </Alert>
              )}
            </Card.Body>
          </Card>
        </Col>

        <Col md={6} className="mb-4">
          <Card className="card-custom">
            <Card.Body>
              <Card.Title>⚠️ Areas for Improvement</Card.Title>
              {analytics.topicWeaknesses ? (
                <ul className="list-unstyled">
                  {analytics.topicWeaknesses.split(',').map((item, idx) => (
                    <li key={idx} className="mb-2 p-2" style={{ backgroundColor: '#f8d7da', borderRadius: '5px' }}>
                      ⚠ {item.trim()}
                    </li>
                  ))}
                </ul>
              ) : (
                <Alert variant="info">
                  Complete more interviews to identify areas for improvement. The system will analyze your weak points and provide recommendations.
                </Alert>
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Performance Summary */}
      {analytics.completedInterviews > 0 && (
        <Row>
          <Col md={12}>
            <Card className="card-custom">
              <Card.Body>
                <Card.Title>📊 Performance Summary</Card.Title>
                <Row>
                  <Col md={4}>
                    <p><strong>Total Completed:</strong> {analytics.completedInterviews}</p>
                    <p><strong>Average Score:</strong> {analytics.averageScore?.toFixed(1)}%</p>
                  </Col>
                  <Col md={4}>
                    <p><strong>Best Score:</strong> {analytics.bestScore?.toFixed(1)}%</p>
                    <p><strong>Worst Score:</strong> {analytics.worstScore?.toFixed(1)}%</p>
                  </Col>
                  <Col md={4}>
                    <p><strong>Last Interview:</strong> {analytics.lastInterviewDate ? new Date(analytics.lastInterviewDate).toLocaleDateString() : 'N/A'}</p>
                    <p><strong>Improvement:</strong> {interviewHistory.length >= 2 ? 
                      ((interviewHistory[interviewHistory.length - 1].score - interviewHistory[0].score).toFixed(1) + '%') : 'N/A'}</p>
                  </Col>
                </Row>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      )}
    </Container>
  );
}

export default AnalyticsPage;

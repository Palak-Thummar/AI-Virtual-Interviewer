import React, { useState, useEffect } from 'react';
import { Container, Card, Row, Col, Alert } from 'react-bootstrap';
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

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, BarElement, Title, Tooltip, Legend);

function AnalyticsPage() {
  const [analytics, setAnalytics] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchAnalytics();
  }, []);

  const fetchAnalytics = async () => {
    try {
      const response = await analyticsService.getMyAnalytics();
      setAnalytics(response.data);
    } catch (error) {
      toast.error('Failed to load analytics');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <Container className="py-5">
        <Alert variant="info">Loading analytics...</Alert>
      </Container>
    );
  }

  const sampleChartData = {
    labels: ['Interview 1', 'Interview 2', 'Interview 3', 'Interview 4', 'Interview 5'],
    datasets: [
      {
        label: 'Score',
        data: [65, 72, 68, 80, 75],
        borderColor: '#667eea',
        backgroundColor: 'rgba(102, 126, 234, 0.1)',
        borderWidth: 2,
      },
    ],
  };

  return (
    <Container className="py-5">
      <ToastContainer position="top-right" autoClose={3000} />

      <div className="mb-4">
        <h1>Your Analytics Dashboard</h1>
        <p className="text-muted">Track your interview preparation progress</p>
      </div>

      {analytics && (
        <>
          <Row className="mb-4">
            <Col md={3} className="mb-3">
              <Card className="card-custom text-center">
                <Card.Body>
                  <Card.Title>Total Interviews</Card.Title>
                  <div style={{ fontSize: '48px', fontWeight: 'bold', color: '#667eea' }}>
                    {analytics.totalInterviews}
                  </div>
                </Card.Body>
              </Card>
            </Col>

            <Col md={3} className="mb-3">
              <Card className="card-custom text-center">
                <Card.Body>
                  <Card.Title>Completed</Card.Title>
                  <div style={{ fontSize: '48px', fontWeight: 'bold', color: '#28a745' }}>
                    {analytics.completedInterviews}
                  </div>
                </Card.Body>
              </Card>
            </Col>

            <Col md={3} className="mb-3">
              <Card className="card-custom text-center">
                <Card.Body>
                  <Card.Title>Average Score</Card.Title>
                  <div style={{ fontSize: '48px', fontWeight: 'bold', color: '#ffc107' }}>
                    {analytics.averageScore?.toFixed(1) || 0}
                  </div>
                </Card.Body>
              </Card>
            </Col>

            <Col md={3} className="mb-3">
              <Card className="card-custom text-center">
                <Card.Body>
                  <Card.Title>Best Score</Card.Title>
                  <div style={{ fontSize: '48px', fontWeight: 'bold', color: '#17a2b8' }}>
                    {analytics.bestScore?.toFixed(1) || 0}
                  </div>
                </Card.Body>
              </Card>
            </Col>
          </Row>

          <Row className="mb-4">
            <Col md={12}>
              <Card className="card-custom">
                <Card.Body>
                  <Card.Title>Score Trend</Card.Title>
                  <div className="analytics-chart">
                    <Line data={sampleChartData} options={{ responsive: true, maintainAspectRatio: false }} />
                  </div>
                </Card.Body>
              </Card>
            </Col>
          </Row>

          <Row>
            <Col md={6} className="mb-4">
              <Card className="card-custom">
                <Card.Body>
                  <Card.Title>Strengths</Card.Title>
                  {analytics.topicStrengths ? (
                    <ul className="list-unstyled">
                      {analytics.topicStrengths.split(',').map((item, idx) => (
                        <li key={idx} className="mb-2">
                          ✓ {item.trim()}
                        </li>
                      ))}
                    </ul>
                  ) : (
                    <p className="text-muted">Complete more interviews to see your strengths</p>
                  )}
                </Card.Body>
              </Card>
            </Col>

            <Col md={6} className="mb-4">
              <Card className="card-custom">
                <Card.Body>
                  <Card.Title>Areas for Improvement</Card.Title>
                  {analytics.topicWeaknesses ? (
                    <ul className="list-unstyled">
                      {analytics.topicWeaknesses.split(',').map((item, idx) => (
                        <li key={idx} className="mb-2">
                          ⚠ {item.trim()}
                        </li>
                      ))}
                    </ul>
                  ) : (
                    <p className="text-muted">Complete more interviews to see areas for improvement</p>
                  )}
                </Card.Body>
              </Card>
            </Col>
          </Row>
        </>
      )}
    </Container>
  );
}

export default AnalyticsPage;

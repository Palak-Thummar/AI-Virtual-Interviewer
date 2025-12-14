import React, { useState, useEffect } from 'react';
import { Container, Card, Row, Col, Alert, Button } from 'react-bootstrap';
import { useParams, useNavigate } from 'react-router-dom';
import { interviewService } from '../services/apiService';
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

function ResultsPage() {
  const { interviewId } = useParams();
  const navigate = useNavigate();
  const [interview, setInterview] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchInterviewResults();
  }, [interviewId]);

  const fetchInterviewResults = async () => {
    try {
      const response = await interviewService.getInterview(interviewId);
      setInterview(response.data);
    } catch (error) {
      toast.error('Failed to load results');
      navigate('/dashboard');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <Container className="py-5">
        <Alert variant="info">Loading results...</Alert>
      </Container>
    );
  }

  return (
    <Container className="py-5">
      <ToastContainer position="top-right" autoClose={3000} />

      {interview && (
        <>
          <div className="mb-4">
            <h1>Interview Results</h1>
            <p className="text-muted">
              {interview.jobRole} - {interview.domain}
            </p>
          </div>

          <Row className="mb-4">
            <Col md={6}>
              <Card className="card-custom text-center">
                <Card.Body>
                  <Card.Title>Overall Score</Card.Title>
                  <div className="score-badge">
                    {interview.overallScore?.toFixed(1) || 0}
                    <span style={{ fontSize: '24px' }}>/100</span>
                  </div>
                  <p className="text-muted mt-3">
                    Questions Answered: {interview.questionsAnswered}/{interview.totalQuestions}
                  </p>
                </Card.Body>
              </Card>
            </Col>

            <Col md={6}>
              <Card className="card-custom">
                <Card.Body>
                  <Card.Title>Summary</Card.Title>
                  <ul className="list-unstyled">
                    <li className="mb-2">
                      <strong>Duration:</strong> Approximately{' '}
                      {interview.endTime
                        ? Math.floor(
                            (new Date(interview.endTime) - new Date(interview.startTime)) / 60000
                          )
                        : 0}{' '}
                      minutes
                    </li>
                    <li className="mb-2">
                      <strong>Status:</strong> <span className="badge bg-success">{interview.status}</span>
                    </li>
                    <li>
                      <strong>Completion Date:</strong>{' '}
                      {new Date(interview.endTime).toLocaleDateString()}
                    </li>
                  </ul>
                </Card.Body>
              </Card>
            </Col>
          </Row>

          <Row className="mb-4">
            <Col>
              <Card className="card-custom">
                <Card.Body>
                  <Card.Title>Performance Analysis</Card.Title>
                  <p className="text-muted mb-4">
                    This section would display detailed analysis of your answers with AI feedback.
                  </p>
                  <Alert variant="light" className="mb-0">
                    <strong>💡 Tip:</strong> Review the detailed feedback in the analytics section to
                    track your progress and identify areas for improvement.
                  </Alert>
                </Card.Body>
              </Card>
            </Col>
          </Row>

          <div className="d-flex gap-2">
            <Button href="/dashboard" variant="primary" className="btn-primary-custom">
              Back to Dashboard
            </Button>
            <Button href="/analytics" variant="outline-primary">
              View Analytics
            </Button>
            <Button href="/dashboard" variant="success" className="ms-auto">
              Start Another Interview
            </Button>
          </div>
        </>
      )}
    </Container>
  );
}

export default ResultsPage;

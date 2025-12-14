import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Button, Form, Alert } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { interviewService } from '../services/apiService';
import { ToastContainer, toast } from 'react-toastify';

function DashboardPage() {
  const [interviews, setInterviews] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showStartModal, setShowStartModal] = useState(false);
  const [startFormData, setStartFormData] = useState({
    jobRole: '',
    domain: '',
    numberOfQuestions: 5,
  });
  const navigate = useNavigate();

  useEffect(() => {
    fetchInterviews();
  }, []);

  const fetchInterviews = async () => {
    try {
      const response = await interviewService.getMyInterviews();
      setInterviews(response.data);
    } catch (error) {
      toast.error('Failed to load interviews');
    } finally {
      setLoading(false);
    }
  };

  const handleStartInterview = async (e) => {
    e.preventDefault();
    try {
      const response = await interviewService.startInterview(startFormData);
      toast.success('Interview started!');
      navigate(`/interview/${response.data.id}`);
    } catch (error) {
      toast.error('Failed to start interview');
    }
  };

  const handleResumeInterview = (interviewId) => {
    navigate(`/interview/${interviewId}`);
  };

  const handleViewResults = (interviewId) => {
    navigate(`/results/${interviewId}`);
  };

  return (
    <Container className="py-5">
      <ToastContainer position="top-right" autoClose={3000} />
      <div className="mb-4">
        <h1 className="mb-2">Welcome to AI Virtual Interviewer</h1>
        <p className="text-muted">Prepare for your interviews with AI-powered mock interviews</p>
      </div>

      <Row className="mb-4">
        <Col md={6}>
          <Card className="card-custom h-100">
            <Card.Body>
              <Card.Title>Start New Interview</Card.Title>
              <Form onSubmit={handleStartInterview}>
                <Form.Group className="mb-3">
                  <Form.Label>Job Role</Form.Label>
                  <Form.Select
                    value={startFormData.jobRole}
                    onChange={(e) => setStartFormData({ ...startFormData, jobRole: e.target.value })}
                    required
                  >
                    <option value="">Select a role</option>
                    <option value="Software Engineer">Software Engineer</option>
                    <option value="Frontend Developer">Frontend Developer</option>
                    <option value="Backend Developer">Backend Developer</option>
                    <option value="Data Scientist">Data Scientist</option>
                  </Form.Select>
                </Form.Group>

                <Form.Group className="mb-3">
                  <Form.Label>Domain</Form.Label>
                  <Form.Select
                    value={startFormData.domain}
                    onChange={(e) => setStartFormData({ ...startFormData, domain: e.target.value })}
                    required
                  >
                    <option value="">Select domain</option>
                    <option value="DSA">Data Structures & Algorithms</option>
                    <option value="System Design">System Design</option>
                    <option value="Technical">Technical</option>
                    <option value="HR">HR/Behavioral</option>
                  </Form.Select>
                </Form.Group>

                <Form.Group className="mb-3">
                  <Form.Label>Number of Questions</Form.Label>
                  <Form.Control
                    type="number"
                    min="1"
                    max="10"
                    value={startFormData.numberOfQuestions}
                    onChange={(e) => setStartFormData({ ...startFormData, numberOfQuestions: parseInt(e.target.value) })}
                  />
                </Form.Group>

                <Button type="submit" className="w-100 btn-primary-custom">
                  Start Interview
                </Button>
              </Form>
            </Card.Body>
          </Card>
        </Col>

        <Col md={6}>
          <Card className="card-custom h-100">
            <Card.Body>
              <Card.Title>Quick Stats</Card.Title>
              <div className="mb-3">
                <p className="mb-1">
                  <strong>Total Interviews:</strong> {interviews.length}
                </p>
                <p className="mb-1">
                  <strong>Completed:</strong> {interviews.filter(i => i.status === 'COMPLETED').length}
                </p>
                <p className="mb-1">
                  <strong>In Progress:</strong> {interviews.filter(i => i.status === 'IN_PROGRESS').length}
                </p>
              </div>
              <Button href="/analytics" className="w-100 btn-primary-custom">
                View Detailed Analytics
              </Button>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      <Row>
        <Col>
          <h3 className="mb-4">Recent Interviews</h3>
          {loading ? (
            <Alert variant="info">Loading...</Alert>
          ) : interviews.length === 0 ? (
            <Alert variant="warning">No interviews yet. Start one to begin preparing!</Alert>
          ) : (
            <div className="row">
              {interviews.map((interview) => (
                <Col md={6} key={interview.id} className="mb-4">
                  <Card className="card-custom h-100">
                    <Card.Body>
                      <Card.Title>{interview.jobRole}</Card.Title>
                      <Card.Text>
                        <p>
                          <strong>Domain:</strong> {interview.domain}
                        </p>
                        <p>
                          <strong>Status:</strong> <span className="badge bg-info">{interview.status}</span>
                        </p>
                        <p>
                          <strong>Progress:</strong> {interview.questionsAnswered}/{interview.totalQuestions}
                        </p>
                        {interview.overallScore && (
                          <p>
                            <strong>Score:</strong> {interview.overallScore.toFixed(2)}/100
                          </p>
                        )}
                      </Card.Text>
                      <div className="d-flex gap-2">
                        {interview.status === 'IN_PROGRESS' && (
                          <Button
                            variant="primary"
                            size="sm"
                            onClick={() => handleResumeInterview(interview.id)}
                            className="btn-primary-custom flex-grow-1"
                          >
                            Resume
                          </Button>
                        )}
                        {interview.status === 'COMPLETED' && (
                          <Button
                            variant="info"
                            size="sm"
                            onClick={() => handleViewResults(interview.id)}
                            className="flex-grow-1"
                          >
                            View Results
                          </Button>
                        )}
                      </div>
                    </Card.Body>
                  </Card>
                </Col>
              ))}
            </div>
          )}
        </Col>
      </Row>
    </Container>
  );
}

export default DashboardPage;

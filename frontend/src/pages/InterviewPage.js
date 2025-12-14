import React, { useState, useEffect } from 'react';
import { Container, Card, Button, Form, Alert, ProgressBar, Row, Col } from 'react-bootstrap';
import { useParams, useNavigate } from 'react-router-dom';
import { interviewService } from '../services/apiService';
import { ToastContainer, toast } from 'react-toastify';
import { FiMic, FiStopCircle, FiSkipForward } from 'react-icons/fi';

function InterviewPage() {
  const { interviewId } = useParams();
  const navigate = useNavigate();
  const [interview, setInterview] = useState(null);
  const [currentQuestion, setCurrentQuestion] = useState(null);
  const [answerText, setAnswerText] = useState('');
  const [timeLeft, setTimeLeft] = useState(0);
  const [isRecording, setIsRecording] = useState(false);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    fetchInterview();
  }, [interviewId]);

  useEffect(() => {
    if (timeLeft > 0) {
      const timer = setTimeout(() => setTimeLeft(timeLeft - 1), 1000);
      return () => clearTimeout(timer);
    }
  }, [timeLeft]);

  const fetchInterview = async () => {
    try {
      const response = await interviewService.getInterview(interviewId);
      setInterview(response.data);
      fetchNextQuestion();
    } catch (error) {
      toast.error('Failed to load interview');
      navigate('/dashboard');
    } finally {
      setLoading(false);
    }
  };

  const fetchNextQuestion = async () => {
    try {
      const response = await interviewService.getNextQuestion(interviewId);
      if (response.data !== 'All questions completed') {
        setCurrentQuestion(response.data);
        setTimeLeft(response.data.timeLimitSeconds || 60);
        setAnswerText('');
      } else {
        handleCompleteInterview();
      }
    } catch (error) {
      toast.error('Failed to load question');
    }
  };

  const handleSubmitAnswer = async () => {
    if (!answerText.trim()) {
      toast.error('Please provide an answer');
      return;
    }

    setSubmitting(true);
    try {
      await interviewService.submitAnswer(interviewId, {
        questionId: currentQuestion.id,
        answerText,
        timeTakenSeconds: currentQuestion.timeLimitSeconds - timeLeft,
      });
      toast.success('Answer submitted successfully!');
      fetchNextQuestion();
    } catch (error) {
      toast.error('Failed to submit answer');
    } finally {
      setSubmitting(false);
    }
  };

  const handleCompleteInterview = async () => {
    try {
      await interviewService.completeInterview(interviewId);
      toast.success('Interview completed!');
      navigate(`/results/${interviewId}`);
    } catch (error) {
      toast.error('Failed to complete interview');
    }
  };

  const toggleRecording = () => {
    setIsRecording(!isRecording);
    if (!isRecording) {
      toast.info('Recording started...');
    } else {
      toast.success('Recording stopped');
    }
  };

  if (loading) {
    return (
      <Container className="py-5">
        <Alert variant="info">Loading interview...</Alert>
      </Container>
    );
  }

  return (
    <Container className="py-5">
      <ToastContainer position="top-right" autoClose={3000} />

      {interview && (
        <>
          <Row className="mb-4">
            <Col md={8}>
              <h2>{interview.jobRole} Interview</h2>
              <p className="text-muted">{interview.domain}</p>
            </Col>
            <Col md={4} className="text-end">
              <div className="timer">
                <span className={timeLeft < 10 ? 'warning' : ''}>
                  {Math.floor(timeLeft / 60)}:{(timeLeft % 60).toString().padStart(2, '0')}
                </span>
              </div>
            </Col>
          </Row>

          <ProgressBar
            now={(interview.questionsAnswered / interview.totalQuestions) * 100}
            label={`Question ${interview.questionsAnswered + 1} of ${interview.totalQuestions}`}
            className="mb-4"
          />

          {currentQuestion && (
            <Card className="card-custom mb-4 question-container">
              <Card.Body>
                <Card.Title>Question {interview.questionsAnswered + 1}</Card.Title>
                <Card.Text className="fs-5 mb-4">
                  {currentQuestion.question}
                </Card.Text>

                {currentQuestion.hints && (
                  <Alert variant="light" className="mb-4">
                    <strong>💡 Hint:</strong> {currentQuestion.hints}
                  </Alert>
                )}

                <Form.Group className="mb-3">
                  <Form.Label>Your Answer</Form.Label>
                  <Form.Control
                    as="textarea"
                    rows={6}
                    placeholder="Type your answer here..."
                    value={answerText}
                    onChange={(e) => setAnswerText(e.target.value)}
                    className="mb-3"
                  />
                </Form.Group>

                <div className="d-flex gap-2 mb-4">
                  <Button
                    variant="outline-secondary"
                    onClick={toggleRecording}
                    className="d-flex align-items-center"
                  >
                    {isRecording ? (
                      <>
                        <FiStopCircle className="me-2" />
                        Stop Recording
                      </>
                    ) : (
                      <>
                        <FiMic className="me-2" />
                        Start Recording
                      </>
                    )}
                  </Button>
                  {isRecording && (
                    <span className="text-danger d-flex align-items-center">
                      🔴 Recording...
                    </span>
                  )}
                </div>

                <div className="d-flex gap-2">
                  <Button
                    variant="primary"
                    className="btn-primary-custom flex-grow-1"
                    onClick={handleSubmitAnswer}
                    disabled={submitting}
                  >
                    {submitting ? 'Submitting...' : 'Submit Answer'}
                  </Button>
                  <Button
                    variant="outline-secondary"
                    onClick={fetchNextQuestion}
                  >
                    <FiSkipForward className="me-2" />
                    Skip
                  </Button>
                </div>
              </Card.Body>
            </Card>
          )}
        </>
      )}
    </Container>
  );
}

export default InterviewPage;

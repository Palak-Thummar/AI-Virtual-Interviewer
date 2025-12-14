import React, { useState, useEffect } from 'react';
import { Container, Card, Button, Form, Table, Alert, Row, Col } from 'react-bootstrap';
import { adminService, questionService } from '../services/apiService';
import { ToastContainer, toast } from 'react-toastify';
import { FiEdit2, FiTrash2, FiPlus } from 'react-icons/fi';

function AdminPage() {
  const [questions, setQuestions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [formData, setFormData] = useState({
    question: '',
    type: 'TECHNICAL',
    domain: '',
    jobRole: '',
    expectedAnswer: '',
    hints: '',
    difficulty: 1,
    timeLimitSeconds: 60,
  });

  useEffect(() => {
    fetchQuestions();
  }, []);

  const fetchQuestions = async () => {
    try {
      const response = await questionService.getAllQuestions();
      setQuestions(response.data);
    } catch (error) {
      toast.error('Failed to load questions');
    } finally {
      setLoading(false);
    }
  };

  const handleFormChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: name === 'difficulty' || name === 'timeLimitSeconds' ? parseInt(value) : value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingId) {
        await adminService.updateQuestion(editingId, formData);
        toast.success('Question updated successfully!');
      } else {
        await adminService.createQuestion(formData);
        toast.success('Question created successfully!');
      }
      fetchQuestions();
      setShowForm(false);
      setEditingId(null);
      resetForm();
    } catch (error) {
      toast.error('Failed to save question');
    }
  };

  const handleEdit = (question) => {
    setFormData(question);
    setEditingId(question.id);
    setShowForm(true);
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this question?')) {
      try {
        await adminService.deleteQuestion(id);
        toast.success('Question deleted successfully!');
        fetchQuestions();
      } catch (error) {
        toast.error('Failed to delete question');
      }
    }
  };

  const resetForm = () => {
    setFormData({
      question: '',
      type: 'TECHNICAL',
      domain: '',
      jobRole: '',
      expectedAnswer: '',
      hints: '',
      difficulty: 1,
      timeLimitSeconds: 60,
    });
  };

  if (loading) {
    return (
      <Container className="py-5">
        <Alert variant="info">Loading...</Alert>
      </Container>
    );
  }

  return (
    <Container className="py-5">
      <ToastContainer position="top-right" autoClose={3000} />

      <div className="mb-4">
        <div className="d-flex justify-content-between align-items-center">
          <div>
            <h1>Admin Panel</h1>
            <p className="text-muted">Manage interview questions</p>
          </div>
          <Button
            className="btn-primary-custom"
            onClick={() => {
              setShowForm(!showForm);
              resetForm();
              setEditingId(null);
            }}
          >
            <FiPlus className="me-2" />
            {showForm ? 'Cancel' : 'Add New Question'}
          </Button>
        </div>
      </div>

      {showForm && (
        <Card className="card-custom mb-4">
          <Card.Body>
            <Card.Title>{editingId ? 'Edit Question' : 'Create New Question'}</Card.Title>
            <Form onSubmit={handleSubmit}>
              <Form.Group className="mb-3">
                <Form.Label>Question</Form.Label>
                <Form.Control
                  as="textarea"
                  rows={3}
                  name="question"
                  value={formData.question}
                  onChange={handleFormChange}
                  required
                />
              </Form.Group>

              <Row>
                <Col md={6}>
                  <Form.Group className="mb-3">
                    <Form.Label>Type</Form.Label>
                    <Form.Select name="type" value={formData.type} onChange={handleFormChange}>
                      <option value="TECHNICAL">Technical</option>
                      <option value="BEHAVIORAL">Behavioral</option>
                      <option value="CODING">Coding</option>
                    </Form.Select>
                  </Form.Group>
                </Col>
                <Col md={6}>
                  <Form.Group className="mb-3">
                    <Form.Label>Domain</Form.Label>
                    <Form.Control
                      name="domain"
                      value={formData.domain}
                      onChange={handleFormChange}
                      placeholder="e.g., DSA, System Design"
                      required
                    />
                  </Form.Group>
                </Col>
              </Row>

              <Row>
                <Col md={6}>
                  <Form.Group className="mb-3">
                    <Form.Label>Job Role</Form.Label>
                    <Form.Control
                      name="jobRole"
                      value={formData.jobRole}
                      onChange={handleFormChange}
                      placeholder="e.g., Software Engineer"
                      required
                    />
                  </Form.Group>
                </Col>
                <Col md={6}>
                  <Form.Group className="mb-3">
                    <Form.Label>Difficulty (1-5)</Form.Label>
                    <Form.Control
                      type="number"
                      name="difficulty"
                      min="1"
                      max="5"
                      value={formData.difficulty}
                      onChange={handleFormChange}
                    />
                  </Form.Group>
                </Col>
              </Row>

              <Form.Group className="mb-3">
                <Form.Label>Expected Answer</Form.Label>
                <Form.Control
                  as="textarea"
                  rows={3}
                  name="expectedAnswer"
                  value={formData.expectedAnswer}
                  onChange={handleFormChange}
                />
              </Form.Group>

              <Row>
                <Col md={6}>
                  <Form.Group className="mb-3">
                    <Form.Label>Hints</Form.Label>
                    <Form.Control
                      name="hints"
                      value={formData.hints}
                      onChange={handleFormChange}
                      placeholder="Optional hints for candidates"
                    />
                  </Form.Group>
                </Col>
                <Col md={6}>
                  <Form.Group className="mb-3">
                    <Form.Label>Time Limit (seconds)</Form.Label>
                    <Form.Control
                      type="number"
                      name="timeLimitSeconds"
                      value={formData.timeLimitSeconds}
                      onChange={handleFormChange}
                    />
                  </Form.Group>
                </Col>
              </Row>

              <Button type="submit" className="btn-primary-custom">
                {editingId ? 'Update Question' : 'Create Question'}
              </Button>
            </Form>
          </Card.Body>
        </Card>
      )}

      <Card className="card-custom">
        <Card.Body>
          <Card.Title>All Questions ({questions.length})</Card.Title>
          {questions.length === 0 ? (
            <Alert variant="info">No questions yet. Create one to get started!</Alert>
          ) : (
            <div className="table-responsive">
              <Table hover>
                <thead>
                  <tr>
                    <th>Question</th>
                    <th>Type</th>
                    <th>Domain</th>
                    <th>Role</th>
                    <th>Difficulty</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {questions.map((q) => (
                    <tr key={q.id}>
                      <td>{q.question.substring(0, 50)}...</td>
                      <td>
                        <span className="badge bg-info">{q.type}</span>
                      </td>
                      <td>{q.domain}</td>
                      <td>{q.jobRole}</td>
                      <td>
                        <span className={`badge bg-${q.difficulty <= 2 ? 'success' : q.difficulty <= 3 ? 'warning' : 'danger'}`}>
                          {'⭐'.repeat(q.difficulty)}
                        </span>
                      </td>
                      <td>
                        <Button
                          variant="outline-secondary"
                          size="sm"
                          onClick={() => handleEdit(q)}
                          className="me-2"
                        >
                          <FiEdit2 />
                        </Button>
                        <Button
                          variant="outline-danger"
                          size="sm"
                          onClick={() => handleDelete(q.id)}
                        >
                          <FiTrash2 />
                        </Button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            </div>
          )}
        </Card.Body>
      </Card>
    </Container>
  );
}

export default AdminPage;

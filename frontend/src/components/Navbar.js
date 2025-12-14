import React from 'react';
import { Navbar as BSNavbar, Nav, Container, Button } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { FiLogOut, FiHome, FiBarChart2, FiSettings } from 'react-icons/fi';

function Navbar() {
  const navigate = useNavigate();
  const user = JSON.parse(localStorage.getItem('user') || '{}');

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    navigate('/login');
  };

  return (
    <BSNavbar bg="light" expand="lg" sticky="top" className="navbar-custom">
      <Container>
        <BSNavbar.Brand href="/dashboard" className="fw-bold">
          🎯 AI Virtual Interviewer
        </BSNavbar.Brand>
        <BSNavbar.Toggle aria-controls="basic-navbar-nav" />
        <BSNavbar.Collapse id="basic-navbar-nav">
          <Nav className="ms-auto">
            <Nav.Link href="/dashboard" className="me-3">
              <FiHome className="me-2" />
              Dashboard
            </Nav.Link>
            <Nav.Link href="/analytics" className="me-3">
              <FiBarChart2 className="me-2" />
              Analytics
            </Nav.Link>
            {user?.role === 'ADMIN' && (
              <Nav.Link href="/admin" className="me-3">
                <FiSettings className="me-2" />
                Admin
              </Nav.Link>
            )}
            <div className="me-3">
              <span className="me-2">Hello, {user?.firstName}</span>
            </div>
            <Button
              variant="danger"
              size="sm"
              onClick={handleLogout}
              className="d-flex align-items-center"
            >
              <FiLogOut className="me-2" />
              Logout
            </Button>
          </Nav>
        </BSNavbar.Collapse>
      </Container>
    </BSNavbar>
  );
}

export default Navbar;

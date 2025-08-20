import './footer.scss';

import React from 'react';
import { Link } from 'react-router-dom';
import { Row, Col } from 'reactstrap';

const Footer = () => (
  <footer className="footer">
    <div className="container">
      <Row className="footer-content">
        <Col lg="4" md="6" className="mb-4">
          <div className="footer-section">
            <h5 className="footer-title">EduPress LMS</h5>
            <p className="footer-description">
              Empowering learners worldwide with quality education and cutting-edge technology. Start your learning journey today and unlock
              your potential.
            </p>
            <div className="social-links">
              <a href="#" className="social-link" aria-label="Facebook">
                <i className="fab fa-facebook"></i>
              </a>
              <a href="#" className="social-link" aria-label="Twitter">
                <i className="fab fa-twitter"></i>
              </a>
              <a href="#" className="social-link" aria-label="LinkedIn">
                <i className="fab fa-linkedin"></i>
              </a>
              <a href="#" className="social-link" aria-label="Instagram">
                <i className="fab fa-instagram"></i>
              </a>
              <a href="#" className="social-link" aria-label="YouTube">
                <i className="fab fa-youtube"></i>
              </a>
            </div>
          </div>
        </Col>

        <Col lg="2" md="6" className="mb-4">
          <div className="footer-section">
            <h6 className="footer-subtitle">Quick Links</h6>
            <ul className="footer-links">
              <li>
                <Link to="/">Home</Link>
              </li>
              <li>
                <Link to="/courses">Courses</Link>
              </li>
              <li>
                <Link to="/register">Sign Up</Link>
              </li>
              <li>
                <Link to="/login">Sign In</Link>
              </li>
            </ul>
          </div>
        </Col>

        <Col lg="2" md="6" className="mb-4">
          <div className="footer-section">
            <h6 className="footer-subtitle">Categories</h6>
            <ul className="footer-links">
              <li>
                <a href="#programming">Programming</a>
              </li>
              <li>
                <a href="#design">Design</a>
              </li>
              <li>
                <a href="#business">Business</a>
              </li>
              <li>
                <a href="#marketing">Marketing</a>
              </li>
            </ul>
          </div>
        </Col>

        <Col lg="2" md="6" className="mb-4">
          <div className="footer-section">
            <h6 className="footer-subtitle">Support</h6>
            <ul className="footer-links">
              <li>
                <a href="#help">Help Center</a>
              </li>
              <li>
                <a href="#contact">Contact Us</a>
              </li>
              <li>
                <a href="#faq">FAQ</a>
              </li>
              <li>
                <a href="#terms">Terms of Service</a>
              </li>
            </ul>
          </div>
        </Col>

        <Col lg="2" md="6" className="mb-4">
          <div className="footer-section">
            <h6 className="footer-subtitle">Company</h6>
            <ul className="footer-links">
              <li>
                <a href="#about">About Us</a>
              </li>
              <li>
                <a href="#careers">Careers</a>
              </li>
              <li>
                <a href="#press">Press</a>
              </li>
              <li>
                <a href="#privacy">Privacy Policy</a>
              </li>
            </ul>
          </div>
        </Col>
      </Row>

      <hr className="footer-divider" />

      <Row className="footer-bottom">
        <Col md="6">
          <p className="copyright">© {new Date().getFullYear()} EduPress LMS. All rights reserved.</p>
        </Col>
        <Col md="6" className="text-md-end">
          <div className="footer-badges">
            <span className="badge-item">
              <i className="fas fa-shield-alt me-1"></i>
              Secure Learning
            </span>
            <span className="badge-item">
              <i className="fas fa-certificate me-1"></i>
              Certified Courses
            </span>
            <span className="badge-item">
              <i className="fas fa-users me-1"></i>
              50k+ Students
            </span>
          </div>
        </Col>
      </Row>
    </div>
  </footer>
);

export default Footer;

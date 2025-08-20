import React from 'react';
import { Link } from 'react-router-dom';
import { Row, Col, Button } from 'reactstrap';
import { useAppSelector } from 'app/config/store';

import './home.scss';

export const Home = () => {
  const account = useAppSelector(state => state.authentication.account);

  return (
    <div className="home-page">
      {/* Hero Section */}
      <section className="hero-section">
        <div className="hero-background"></div>
        <div className="container">
          <Row className="align-items-center min-vh-100">
            <Col lg="6">
              <div className="hero-content">
                <h1 className="hero-title">
                  Learn Without <span className="text-primary">Limits</span>
                </h1>
                <p className="hero-subtitle">
                  Access thousands of courses from world-class instructors. Start your learning journey today and unlock your potential.
                </p>
                <div className="hero-actions">
                  {account?.login ? (
                    <Link to="/courses" className="btn btn-primary btn-lg me-3">
                      Browse Courses
                    </Link>
                  ) : (
                    <>
                      <Link to="/register" className="btn btn-primary btn-lg me-3">
                        Get Started
                      </Link>
                      <Link to="/login" className="btn btn-outline-primary btn-lg">
                        Sign In
                      </Link>
                    </>
                  )}
                </div>
                <div className="hero-stats">
                  <div className="stat-item">
                    <span className="stat-number">10,000+</span>
                    <span className="stat-label">Students</span>
                  </div>
                  <div className="stat-item">
                    <span className="stat-number">500+</span>
                    <span className="stat-label">Courses</span>
                  </div>
                  <div className="stat-item">
                    <span className="stat-number">50+</span>
                    <span className="stat-label">Expert Instructors</span>
                  </div>
                </div>
              </div>
            </Col>
            <Col lg="6">
              <div className="hero-image">
                <div className="image-placeholder">
                  <i className="fas fa-graduation-cap"></i>
                </div>
              </div>
            </Col>
          </Row>
        </div>
      </section>

      {/* Features Section */}
      <section className="features-section">
        <div className="container">
          <Row>
            <Col lg="8" className="mx-auto text-center mb-5">
              <h2 className="section-title">Why Choose EduPress?</h2>
              <p className="section-subtitle">
                We provide the best learning experience with cutting-edge technology and expert instructors.
              </p>
            </Col>
          </Row>
          <Row className="g-4">
            <Col lg="4" md="6">
              <div className="feature-card">
                <div className="feature-icon">
                  <i className="fas fa-play-circle"></i>
                </div>
                <h3 className="feature-title">Interactive Learning</h3>
                <p className="feature-description">
                  Engage with interactive video lessons, quizzes, and hands-on projects designed to enhance your learning experience.
                </p>
              </div>
            </Col>
            <Col lg="4" md="6">
              <div className="feature-card">
                <div className="feature-icon">
                  <i className="fas fa-certificate"></i>
                </div>
                <h3 className="feature-title">Certificates</h3>
                <p className="feature-description">
                  Earn industry-recognized certificates upon completion of courses to showcase your new skills to employers.
                </p>
              </div>
            </Col>
            <Col lg="4" md="6">
              <div className="feature-card">
                <div className="feature-icon">
                  <i className="fas fa-users"></i>
                </div>
                <h3 className="feature-title">Expert Instructors</h3>
                <p className="feature-description">
                  Learn from industry professionals and experienced educators who are passionate about sharing their knowledge.
                </p>
              </div>
            </Col>
            <Col lg="4" md="6">
              <div className="feature-card">
                <div className="feature-icon">
                  <i className="fas fa-mobile-alt"></i>
                </div>
                <h3 className="feature-title">Learn Anywhere</h3>
                <p className="feature-description">
                  Access your courses on any device, anywhere, anytime. Continue learning on your schedule with our mobile-friendly
                  platform.
                </p>
              </div>
            </Col>
            <Col lg="4" md="6">
              <div className="feature-card">
                <div className="feature-icon">
                  <i className="fas fa-chart-line"></i>
                </div>
                <h3 className="feature-title">Track Progress</h3>
                <p className="feature-description">
                  Monitor your learning progress with detailed analytics and personalized recommendations to achieve your goals faster.
                </p>
              </div>
            </Col>
            <Col lg="4" md="6">
              <div className="feature-card">
                <div className="feature-icon">
                  <i className="fas fa-comments"></i>
                </div>
                <h3 className="feature-title">Community Support</h3>
                <p className="feature-description">
                  Join a vibrant community of learners, participate in discussions, and get help from peers and instructors.
                </p>
              </div>
            </Col>
          </Row>
        </div>
      </section>

      {/* Popular Courses Section */}
      <section className="popular-courses-section">
        <div className="container">
          <Row>
            <Col lg="8" className="mx-auto text-center mb-5">
              <h2 className="section-title">Popular Courses</h2>
              <p className="section-subtitle">Discover our most popular courses chosen by thousands of students worldwide.</p>
            </Col>
          </Row>
          <Row className="g-4">
            <Col lg="4" md="6">
              <div className="course-preview-card">
                <div className="course-preview-image">
                  <i className="fab fa-react"></i>
                </div>
                <div className="course-preview-content">
                  <h4 className="course-preview-title">React Development Masterclass</h4>
                  <p className="course-preview-description">Master React from basics to advanced concepts</p>
                  <div className="course-preview-footer">
                    <span className="course-preview-price">$99</span>
                    <span className="course-preview-rating">
                      <i className="fas fa-star"></i> 4.8
                    </span>
                  </div>
                </div>
              </div>
            </Col>
            <Col lg="4" md="6">
              <div className="course-preview-card">
                <div className="course-preview-image">
                  <i className="fab fa-python"></i>
                </div>
                <div className="course-preview-content">
                  <h4 className="course-preview-title">Python for Beginners</h4>
                  <p className="course-preview-description">Learn Python programming from scratch</p>
                  <div className="course-preview-footer">
                    <span className="course-preview-price">$79</span>
                    <span className="course-preview-rating">
                      <i className="fas fa-star"></i> 4.9
                    </span>
                  </div>
                </div>
              </div>
            </Col>
            <Col lg="4" md="6">
              <div className="course-preview-card">
                <div className="course-preview-image">
                  <i className="fas fa-brain"></i>
                </div>
                <div className="course-preview-content">
                  <h4 className="course-preview-title">Machine Learning Fundamentals</h4>
                  <p className="course-preview-description">Introduction to AI and machine learning</p>
                  <div className="course-preview-footer">
                    <span className="course-preview-price">$149</span>
                    <span className="course-preview-rating">
                      <i className="fas fa-star"></i> 4.7
                    </span>
                  </div>
                </div>
              </div>
            </Col>
          </Row>
          <Row className="mt-5">
            <Col className="text-center">
              <Link to="/courses" className="btn btn-primary btn-lg">
                View All Courses
              </Link>
            </Col>
          </Row>
        </div>
      </section>

      {/* Stats Section */}
      <section className="stats-section">
        <div className="container">
          <Row className="g-4">
            <Col lg="3" md="6" className="text-center">
              <div className="stat-card">
                <div className="stat-icon">
                  <i className="fas fa-users"></i>
                </div>
                <div className="stat-number">50,000+</div>
                <div className="stat-label">Active Students</div>
              </div>
            </Col>
            <Col lg="3" md="6" className="text-center">
              <div className="stat-card">
                <div className="stat-icon">
                  <i className="fas fa-book"></i>
                </div>
                <div className="stat-number">1,500+</div>
                <div className="stat-label">Courses Available</div>
              </div>
            </Col>
            <Col lg="3" md="6" className="text-center">
              <div className="stat-card">
                <div className="stat-icon">
                  <i className="fas fa-chalkboard-teacher"></i>
                </div>
                <div className="stat-number">200+</div>
                <div className="stat-label">Expert Instructors</div>
              </div>
            </Col>
            <Col lg="3" md="6" className="text-center">
              <div className="stat-card">
                <div className="stat-icon">
                  <i className="fas fa-certificate"></i>
                </div>
                <div className="stat-number">25,000+</div>
                <div className="stat-label">Certificates Issued</div>
              </div>
            </Col>
          </Row>
        </div>
      </section>

      {/* CTA Section */}
      <section className="cta-section">
        <div className="container">
          <Row>
            <Col lg="8" className="mx-auto text-center">
              <h2 className="cta-title">Ready to Start Learning?</h2>
              <p className="cta-subtitle">Join thousands of students who have already transformed their careers with our courses.</p>
              <div className="cta-actions">
                {account?.login ? (
                  <Link to="/courses" className="btn btn-primary btn-lg">
                    Explore Courses
                  </Link>
                ) : (
                  <>
                    <Link to="/register" className="btn btn-primary btn-lg me-3">
                      Start Free Trial
                    </Link>
                    <Link to="/courses" className="btn btn-outline-light btn-lg">
                      Browse Courses
                    </Link>
                  </>
                )}
              </div>
            </Col>
          </Row>
        </div>
      </section>
    </div>
  );
};

export default Home;

import React, { useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { Alert, Button, Col, Progress, Row } from 'reactstrap';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntity as getCourse } from 'app/entities/course/course.reducer';
import { createForCourse, getMyEnrollmentForCourse } from 'app/entities/enrollment/enrollment.reducer';
import { getByCourseId as getLessonsForCourse } from 'app/entities/lesson/lesson.reducer';
import { getEntities as getCertificates } from 'app/entities/certificate/certificate.reducer';

const CourseDetailPage = () => {
  const { id } = useParams();
  const dispatch = useAppDispatch();
  const { entity: course, loading } = useAppSelector(state => state.course);
  const auth = useAppSelector(state => state.authentication);
  const enrollmentState = useAppSelector(state => state.enrollment);

  useEffect(() => {
    if (id) dispatch(getCourse(id));
    if (id && auth.isAuthenticated) dispatch(getMyEnrollmentForCourse(Number(id)));
    if (id) dispatch(getLessonsForCourse(Number(id)));
  }, [dispatch, id, auth.isAuthenticated]);

  const enrollment = enrollmentState.entity;
  const isEnrolled = !!enrollment && !!enrollment.id;
  const percent = enrollment?.progressPercentage ?? 0;
  const isCompleted = enrollment?.status === 'COMPLETED' || percent >= 100;
  const lessons = useAppSelector(state => state.lesson.entities);
  const certificates = useAppSelector(state => state.certificate.entities);

  useEffect(() => {
    if (enrollment?.id) {
      // fetch certificates for this enrollment id
      dispatch(getCertificates({ page: 0, size: 20, sort: `id,asc&enrollmentId.equals=${enrollment.id}` } as any));
    }
  }, [dispatch, enrollment?.id]);

  const continueHref = () => {
    if (!lessons?.length || !course?.id) return undefined;
    // naive: if enrollment.progress stores last lessonId, use it; else first lesson
    let lastLessonId: number | undefined;
    try {
      if (enrollment?.progress) {
        const p = JSON.parse(enrollment.progress);
        if (p?.lastLessonId) lastLessonId = Number(p.lastLessonId);
      }
    } catch (_) {
      // ignore JSON parse errors
    }
    const target = lastLessonId && lessons.find(l => l.id === lastLessonId) ? lastLessonId : lessons[0].id;
    return target ? `/lesson/${target}` : undefined;
  };

  const handleEnroll = async () => {
    if (!auth.isAuthenticated) {
      window.location.href = '/login';
      return;
    }
    if (id) await dispatch(createForCourse(Number(id)));
    if (id) await dispatch(getMyEnrollmentForCourse(Number(id)));
  };

  if (loading)
    return (
      <div className="loading-container">
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
      </div>
    );

  if (!course || !course.id)
    return (
      <div className="error-container">
        <div className="error-message">
          <i className="fas fa-exclamation-triangle"></i>
          <h3>Course not found</h3>
          <p>The course you&apos;re looking for doesn&apos;t exist or has been removed.</p>
          <Link to="/courses" className="btn btn-primary">
            Browse All Courses
          </Link>
        </div>
      </div>
    );

  return (
    <div className="course-detail-page">
      <div className="course-hero">
        <div className="container">
          <Row className="align-items-center">
            <Col lg="8">
              <nav aria-label="breadcrumb" className="mb-3">
                <ol className="breadcrumb">
                  <li className="breadcrumb-item">
                    <Link to="/courses">Courses</Link>
                  </li>
                  <li className="breadcrumb-item active" aria-current="page">
                    {course.title}
                  </li>
                </ol>
              </nav>
              <h1 className="course-hero-title">{course.title}</h1>
              {course.shortDescription && <p className="course-hero-subtitle">{course.shortDescription}</p>}
              <div className="course-meta">
                <span className="course-price-hero">{course.price ? `$${course.price}` : 'Free'}</span>
                {course.originalPrice && <span className="course-original-price">${course.originalPrice}</span>}
              </div>
            </Col>
          </Row>
        </div>
      </div>

      <div className="container py-5">
        <Row className="g-5">
          <Col lg="8">
            <div className="course-content">
              {course.thumbnailUrl && (
                <div className="course-thumbnail mb-4">
                  <img src={course.thumbnailUrl} alt={course.title} className="img-fluid rounded-3" />
                </div>
              )}

              <div className="course-description">
                <h2 className="section-title">About This Course</h2>
                {course.description ? (
                  <div dangerouslySetInnerHTML={{ __html: course.description }} />
                ) : (
                  <p>No description available for this course.</p>
                )}
              </div>
            </div>
          </Col>

          <Col lg="4">
            <div className="course-sidebar">
              <div className="enrollment-card">
                <div className="enrollment-header">
                  <div className="price-section">
                    <div className="current-price">{course.price ? `$${course.price}` : 'Free'}</div>
                    {course.originalPrice && <div className="original-price">${course.originalPrice}</div>}
                  </div>
                </div>

                {auth.isAuthenticated && isEnrolled && (
                  <div className="progress-section">
                    <h6 className="progress-title">Your Progress</h6>
                    <Progress value={percent} className="custom-progress" />
                    <div className="progress-stats">
                      <span>{percent || 0}% complete</span>
                      {isCompleted ? (
                        <span className="status-completed">
                          <i className="fas fa-check-circle"></i> Completed
                        </span>
                      ) : (
                        <span className="status-progress">In progress</span>
                      )}
                    </div>
                  </div>
                )}

                {auth.isAuthenticated && isCompleted && isEnrolled && (
                  <div className="certificate-section">
                    {certificates && certificates.length > 0 ? (
                      <a href={certificates[0].url} target="_blank" rel="noreferrer" className="btn btn-success btn-certificate">
                        <i className="fas fa-certificate me-2"></i>
                        View Certificate
                      </a>
                    ) : (
                      <Alert color="success" className="certificate-alert">
                        <i className="fas fa-check-circle me-2"></i>
                        Certificate generated. Check your profile if not visible yet.
                      </Alert>
                    )}
                  </div>
                )}

                <div className="action-buttons">
                  <Button
                    color="primary"
                    size="lg"
                    className="enroll-btn"
                    onClick={handleEnroll}
                    disabled={auth.isAuthenticated && isEnrolled}
                  >
                    {auth.isAuthenticated ? (
                      isEnrolled ? (
                        <>
                          <i className="fas fa-check me-2"></i>
                          Enrolled
                        </>
                      ) : (
                        'Enroll Now'
                      )
                    ) : (
                      'Sign in to Enroll'
                    )}
                  </Button>

                  {auth.isAuthenticated && isEnrolled && continueHref() && (
                    <Link to={continueHref() || ''} className="btn btn-outline-primary btn-lg continue-btn">
                      <i className="fas fa-play me-2"></i>
                      Continue Learning
                    </Link>
                  )}

                  <Link to="/courses" className="btn btn-outline-secondary btn-lg back-btn">
                    <i className="fas fa-arrow-left me-2"></i>
                    Back to Courses
                  </Link>
                </div>
              </div>
            </div>
          </Col>
        </Row>
      </div>
    </div>
  );
};

export default CourseDetailPage;

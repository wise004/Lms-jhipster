import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Button, Card, CardBody, CardImg, CardText, CardTitle, Col, Input, Row } from 'reactstrap';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getCourses } from 'app/entities/course/course.reducer';

const PAGE_SIZE = 12;

const CoursesPage = () => {
  const dispatch = useAppDispatch();
  const { entities: courses, loading, totalItems } = useAppSelector(state => state.course);
  const [page, setPage] = useState(0);
  const [query, setQuery] = useState('');

  useEffect(() => {
    dispatch(getCourses({ page: 0, size: PAGE_SIZE, sort: 'id,desc' }));
    setPage(0);
  }, [dispatch]);

  const loadMore = () => {
    const next = page + 1;
    setPage(next);
    dispatch(getCourses({ page: next, size: PAGE_SIZE, sort: 'id,desc' }));
  };

  const filtered = query ? courses.filter(c => (c.title || '').toLowerCase().includes(query.toLowerCase())) : courses;

  return (
    <div className="courses-page">
      <div className="hero-section hero-section-sm">
        <div className="container">
          <div className="row align-items-center">
            <div className="col-md-8">
              <h1 className="hero-title">Explore Our Courses</h1>
              <p className="hero-subtitle">Discover thousands of courses taught by expert instructors</p>
            </div>
            <div className="col-md-4">
              <div className="search-box">
                <Input
                  type="search"
                  placeholder="Search courses..."
                  value={query}
                  onChange={e => setQuery(e.target.value)}
                  className="search-input"
                />
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="container py-5">
        <div className="courses-grid">
          <Row className="g-4">
            {filtered.map(c => (
              <Col key={c.id} xl="3" lg="4" md="6" className="mb-4">
                <Card className="course-card h-100">
                  <div className="course-image">
                    {c.thumbnailUrl ? (
                      <CardImg top src={c.thumbnailUrl} alt={c.title} />
                    ) : (
                      <div className="course-placeholder">
                        <i className="fas fa-book"></i>
                      </div>
                    )}
                  </div>
                  <CardBody className="d-flex flex-column">
                    <CardTitle tag="h5" className="course-title">
                      {c.title}
                    </CardTitle>
                    {c.shortDescription && <CardText className="course-description flex-grow-1">{c.shortDescription}</CardText>}
                    <div className="course-footer">
                      <div className="course-price">{c.price ? `$${c.price}` : 'Free'}</div>
                      <Link to={`/courses/${c.id}`} className="btn btn-primary btn-sm course-btn">
                        View Course
                      </Link>
                    </div>
                  </CardBody>
                </Card>
              </Col>
            ))}
          </Row>
        </div>

        {courses.length < (totalItems ?? 0) && (
          <div className="text-center mt-5">
            <Button color="primary" size="lg" disabled={loading} onClick={loadMore} className="load-more-btn">
              {loading ? (
                <>
                  <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                  Loading...
                </>
              ) : (
                'Load More Courses'
              )}
            </Button>
          </div>
        )}
      </div>
    </div>
  );
};

export default CoursesPage;

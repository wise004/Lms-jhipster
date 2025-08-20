import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './course.reducer';

export const CourseDetail = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const courseEntity = useAppSelector(state => state.course.entity);
  const enroll = async () => {
    if (!courseEntity?.id) return;
    try {
      const resp = await axios.post('/api/enrollments', { course: { id: courseEntity.id } });
      const newId = resp.data?.id;
      if (newId) navigate(`/enrollment/${newId}`);
    } catch (e) {
      // no-op
    }
  };
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="courseDetailsHeading">
          <Translate contentKey="edupressApp.course.detail.title">Course</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{courseEntity.id}</dd>
          <dt>
            <span id="title">
              <Translate contentKey="edupressApp.course.title">Title</Translate>
            </span>
          </dt>
          <dd>{courseEntity.title}</dd>
          <dt>
            <span id="slug">
              <Translate contentKey="edupressApp.course.slug">Slug</Translate>
            </span>
          </dt>
          <dd>{courseEntity.slug}</dd>
          <dt>
            <span id="shortDescription">
              <Translate contentKey="edupressApp.course.shortDescription">Short Description</Translate>
            </span>
          </dt>
          <dd>{courseEntity.shortDescription}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="edupressApp.course.description">Description</Translate>
            </span>
          </dt>
          <dd>{courseEntity.description}</dd>
          <dt>
            <span id="thumbnailUrl">
              <Translate contentKey="edupressApp.course.thumbnailUrl">Thumbnail Url</Translate>
            </span>
          </dt>
          <dd>{courseEntity.thumbnailUrl}</dd>
          <dt>
            <span id="price">
              <Translate contentKey="edupressApp.course.price">Price</Translate>
            </span>
          </dt>
          <dd>{courseEntity.price}</dd>
          <dt>
            <span id="originalPrice">
              <Translate contentKey="edupressApp.course.originalPrice">Original Price</Translate>
            </span>
          </dt>
          <dd>{courseEntity.originalPrice}</dd>
          <dt>
            <span id="level">
              <Translate contentKey="edupressApp.course.level">Level</Translate>
            </span>
          </dt>
          <dd>{courseEntity.level}</dd>
          <dt>
            <span id="language">
              <Translate contentKey="edupressApp.course.language">Language</Translate>
            </span>
          </dt>
          <dd>{courseEntity.language}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="edupressApp.course.status">Status</Translate>
            </span>
          </dt>
          <dd>{courseEntity.status}</dd>
          <dt>
            <span id="isPublished">
              <Translate contentKey="edupressApp.course.isPublished">Is Published</Translate>
            </span>
          </dt>
          <dd>{courseEntity.isPublished ? 'true' : 'false'}</dd>
          <dt>
            <span id="isFeatured">
              <Translate contentKey="edupressApp.course.isFeatured">Is Featured</Translate>
            </span>
          </dt>
          <dd>{courseEntity.isFeatured ? 'true' : 'false'}</dd>
          <dt>
            <span id="averageRating">
              <Translate contentKey="edupressApp.course.averageRating">Average Rating</Translate>
            </span>
          </dt>
          <dd>{courseEntity.averageRating}</dd>
          <dt>
            <span id="enrollmentCount">
              <Translate contentKey="edupressApp.course.enrollmentCount">Enrollment Count</Translate>
            </span>
          </dt>
          <dd>{courseEntity.enrollmentCount}</dd>
          <dt>
            <Translate contentKey="edupressApp.course.instructor">Instructor</Translate>
          </dt>
          <dd>{courseEntity.instructor ? courseEntity.instructor.email : ''}</dd>
          <dt>
            <Translate contentKey="edupressApp.course.category">Category</Translate>
          </dt>
          <dd>{courseEntity.category ? courseEntity.category.name : ''}</dd>
        </dl>
        <Button tag={Link} to="/course" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/course/${courseEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />
          <span className="d-none d-md-inline">
            &nbsp;<Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
        &nbsp;
        <Button color="success" onClick={enroll}>
          <FontAwesomeIcon icon="sign-in-alt" />
          <span className="d-none d-md-inline">&nbsp;Enroll</span>
        </Button>
      </Col>
    </Row>
  );
};

export default CourseDetail;

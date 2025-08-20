import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './lesson.reducer';

export const LessonDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const lessonEntity = useAppSelector(state => state.lesson.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="lessonDetailsHeading">
          <Translate contentKey="edupressApp.lesson.detail.title">Lesson</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{lessonEntity.id}</dd>
          <dt>
            <span id="title">
              <Translate contentKey="edupressApp.lesson.title">Title</Translate>
            </span>
          </dt>
          <dd>{lessonEntity.title}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="edupressApp.lesson.description">Description</Translate>
            </span>
          </dt>
          <dd>{lessonEntity.description}</dd>
          <dt>
            <span id="content">
              <Translate contentKey="edupressApp.lesson.content">Content</Translate>
            </span>
          </dt>
          <dd>{lessonEntity.content}</dd>
          <dt>
            <span id="videoUrl">
              <Translate contentKey="edupressApp.lesson.videoUrl">Video Url</Translate>
            </span>
          </dt>
          <dd>{lessonEntity.videoUrl}</dd>
          <dt>
            <span id="duration">
              <Translate contentKey="edupressApp.lesson.duration">Duration</Translate>
            </span>
          </dt>
          <dd>{lessonEntity.duration}</dd>
          <dt>
            <span id="type">
              <Translate contentKey="edupressApp.lesson.type">Type</Translate>
            </span>
          </dt>
          <dd>{lessonEntity.type}</dd>
          <dt>
            <span id="isFree">
              <Translate contentKey="edupressApp.lesson.isFree">Is Free</Translate>
            </span>
          </dt>
          <dd>{lessonEntity.isFree ? 'true' : 'false'}</dd>
          <dt>
            <span id="sortOrder">
              <Translate contentKey="edupressApp.lesson.sortOrder">Sort Order</Translate>
            </span>
          </dt>
          <dd>{lessonEntity.sortOrder}</dd>
          <dt>
            <span id="isPublished">
              <Translate contentKey="edupressApp.lesson.isPublished">Is Published</Translate>
            </span>
          </dt>
          <dd>{lessonEntity.isPublished ? 'true' : 'false'}</dd>
          <dt>
            <Translate contentKey="edupressApp.lesson.course">Course</Translate>
          </dt>
          <dd>{lessonEntity.course ? lessonEntity.course.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/lesson" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/lesson/${lessonEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default LessonDetail;

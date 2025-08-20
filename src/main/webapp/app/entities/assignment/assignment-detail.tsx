import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './assignment.reducer';

export const AssignmentDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const assignmentEntity = useAppSelector(state => state.assignment.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="assignmentDetailsHeading">
          <Translate contentKey="edupressApp.assignment.detail.title">Assignment</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{assignmentEntity.id}</dd>
          <dt>
            <span id="title">
              <Translate contentKey="edupressApp.assignment.title">Title</Translate>
            </span>
          </dt>
          <dd>{assignmentEntity.title}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="edupressApp.assignment.description">Description</Translate>
            </span>
          </dt>
          <dd>{assignmentEntity.description}</dd>
          <dt>
            <span id="instructions">
              <Translate contentKey="edupressApp.assignment.instructions">Instructions</Translate>
            </span>
          </dt>
          <dd>{assignmentEntity.instructions}</dd>
          <dt>
            <span id="dueDate">
              <Translate contentKey="edupressApp.assignment.dueDate">Due Date</Translate>
            </span>
          </dt>
          <dd>{assignmentEntity.dueDate ? <TextFormat value={assignmentEntity.dueDate} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="maxPoints">
              <Translate contentKey="edupressApp.assignment.maxPoints">Max Points</Translate>
            </span>
          </dt>
          <dd>{assignmentEntity.maxPoints}</dd>
          <dt>
            <span id="submissionType">
              <Translate contentKey="edupressApp.assignment.submissionType">Submission Type</Translate>
            </span>
          </dt>
          <dd>{assignmentEntity.submissionType}</dd>
          <dt>
            <span id="allowedFileTypes">
              <Translate contentKey="edupressApp.assignment.allowedFileTypes">Allowed File Types</Translate>
            </span>
          </dt>
          <dd>{assignmentEntity.allowedFileTypes}</dd>
          <dt>
            <span id="maxFileSize">
              <Translate contentKey="edupressApp.assignment.maxFileSize">Max File Size</Translate>
            </span>
          </dt>
          <dd>{assignmentEntity.maxFileSize}</dd>
          <dt>
            <span id="isPublished">
              <Translate contentKey="edupressApp.assignment.isPublished">Is Published</Translate>
            </span>
          </dt>
          <dd>{assignmentEntity.isPublished ? 'true' : 'false'}</dd>
          <dt>
            <span id="allowLateSubmission">
              <Translate contentKey="edupressApp.assignment.allowLateSubmission">Allow Late Submission</Translate>
            </span>
          </dt>
          <dd>{assignmentEntity.allowLateSubmission ? 'true' : 'false'}</dd>
          <dt>
            <span id="lateSubmissionPenalty">
              <Translate contentKey="edupressApp.assignment.lateSubmissionPenalty">Late Submission Penalty</Translate>
            </span>
          </dt>
          <dd>{assignmentEntity.lateSubmissionPenalty}</dd>
          <dt>
            <span id="sortOrder">
              <Translate contentKey="edupressApp.assignment.sortOrder">Sort Order</Translate>
            </span>
          </dt>
          <dd>{assignmentEntity.sortOrder}</dd>
          <dt>
            <Translate contentKey="edupressApp.assignment.course">Course</Translate>
          </dt>
          <dd>{assignmentEntity.course ? assignmentEntity.course.id : ''}</dd>
          <dt>
            <Translate contentKey="edupressApp.assignment.lesson">Lesson</Translate>
          </dt>
          <dd>{assignmentEntity.lesson ? assignmentEntity.lesson.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/assignment" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/assignment/${assignmentEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default AssignmentDetail;

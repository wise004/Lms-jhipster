import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './assignment-submission.reducer';

export const AssignmentSubmissionDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const assignmentSubmissionEntity = useAppSelector(state => state.assignmentSubmission.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="assignmentSubmissionDetailsHeading">
          <Translate contentKey="edupressApp.assignmentSubmission.detail.title">AssignmentSubmission</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{assignmentSubmissionEntity.id}</dd>
          <dt>
            <span id="submittedAt">
              <Translate contentKey="edupressApp.assignmentSubmission.submittedAt">Submitted At</Translate>
            </span>
          </dt>
          <dd>
            {assignmentSubmissionEntity.submittedAt ? (
              <TextFormat value={assignmentSubmissionEntity.submittedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="submissionText">
              <Translate contentKey="edupressApp.assignmentSubmission.submissionText">Submission Text</Translate>
            </span>
          </dt>
          <dd>{assignmentSubmissionEntity.submissionText}</dd>
          <dt>
            <span id="fileUrls">
              <Translate contentKey="edupressApp.assignmentSubmission.fileUrls">File Urls</Translate>
            </span>
          </dt>
          <dd>{assignmentSubmissionEntity.fileUrls}</dd>
          <dt>
            <span id="grade">
              <Translate contentKey="edupressApp.assignmentSubmission.grade">Grade</Translate>
            </span>
          </dt>
          <dd>{assignmentSubmissionEntity.grade}</dd>
          <dt>
            <span id="feedback">
              <Translate contentKey="edupressApp.assignmentSubmission.feedback">Feedback</Translate>
            </span>
          </dt>
          <dd>{assignmentSubmissionEntity.feedback}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="edupressApp.assignmentSubmission.status">Status</Translate>
            </span>
          </dt>
          <dd>{assignmentSubmissionEntity.status}</dd>
          <dt>
            <Translate contentKey="edupressApp.assignmentSubmission.assignment">Assignment</Translate>
          </dt>
          <dd>{assignmentSubmissionEntity.assignment ? assignmentSubmissionEntity.assignment.id : ''}</dd>
          <dt>
            <Translate contentKey="edupressApp.assignmentSubmission.student">Student</Translate>
          </dt>
          <dd>{assignmentSubmissionEntity.student ? assignmentSubmissionEntity.student.email : ''}</dd>
        </dl>
        <Button tag={Link} to="/assignment-submission" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/assignment-submission/${assignmentSubmissionEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default AssignmentSubmissionDetail;

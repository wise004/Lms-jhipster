import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './enrollment.reducer';

export const EnrollmentDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const enrollmentEntity = useAppSelector(state => state.enrollment.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="enrollmentDetailsHeading">
          <Translate contentKey="edupressApp.enrollment.detail.title">Enrollment</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{enrollmentEntity.id}</dd>
          <dt>
            <span id="enrollmentDate">
              <Translate contentKey="edupressApp.enrollment.enrollmentDate">Enrollment Date</Translate>
            </span>
          </dt>
          <dd>
            {enrollmentEntity.enrollmentDate ? (
              <TextFormat value={enrollmentEntity.enrollmentDate} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="progressPercentage">
              <Translate contentKey="edupressApp.enrollment.progressPercentage">Progress Percentage</Translate>
            </span>
          </dt>
          <dd>{enrollmentEntity.progressPercentage}</dd>
          <dt>
            <span id="progress">
              <Translate contentKey="edupressApp.enrollment.progress">Progress</Translate>
            </span>
          </dt>
          <dd>{enrollmentEntity.progress}</dd>
          <dt>
            <span id="lastAccessedAt">
              <Translate contentKey="edupressApp.enrollment.lastAccessedAt">Last Accessed At</Translate>
            </span>
          </dt>
          <dd>
            {enrollmentEntity.lastAccessedAt ? (
              <TextFormat value={enrollmentEntity.lastAccessedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="status">
              <Translate contentKey="edupressApp.enrollment.status">Status</Translate>
            </span>
          </dt>
          <dd>{enrollmentEntity.status}</dd>
          <dt>
            <span id="paymentStatus">
              <Translate contentKey="edupressApp.enrollment.paymentStatus">Payment Status</Translate>
            </span>
          </dt>
          <dd>{enrollmentEntity.paymentStatus}</dd>
          <dt>
            <span id="amountPaid">
              <Translate contentKey="edupressApp.enrollment.amountPaid">Amount Paid</Translate>
            </span>
          </dt>
          <dd>{enrollmentEntity.amountPaid}</dd>
          <dt>
            <span id="transactionId">
              <Translate contentKey="edupressApp.enrollment.transactionId">Transaction Id</Translate>
            </span>
          </dt>
          <dd>{enrollmentEntity.transactionId}</dd>
          <dt>
            <span id="completedAt">
              <Translate contentKey="edupressApp.enrollment.completedAt">Completed At</Translate>
            </span>
          </dt>
          <dd>
            {enrollmentEntity.completedAt ? <TextFormat value={enrollmentEntity.completedAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <Translate contentKey="edupressApp.enrollment.course">Course</Translate>
          </dt>
          <dd>{enrollmentEntity.course ? enrollmentEntity.course.id : ''}</dd>
          <dt>
            <Translate contentKey="edupressApp.enrollment.student">Student</Translate>
          </dt>
          <dd>{enrollmentEntity.student ? enrollmentEntity.student.email : ''}</dd>
        </dl>
        <Button tag={Link} to="/enrollment" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/enrollment/${enrollmentEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default EnrollmentDetail;

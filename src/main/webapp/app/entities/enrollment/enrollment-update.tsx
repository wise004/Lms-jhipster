import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getCourses } from 'app/entities/course/course.reducer';
import { getEntities as getAppUsers } from 'app/entities/app-user/app-user.reducer';
import { EnrollmentStatus } from 'app/shared/model/enumerations/enrollment-status.model';
import { PaymentStatus } from 'app/shared/model/enumerations/payment-status.model';
import { createEntity, getEntity, reset, updateEntity } from './enrollment.reducer';

export const EnrollmentUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const courses = useAppSelector(state => state.course.entities);
  const appUsers = useAppSelector(state => state.appUser.entities);
  const enrollmentEntity = useAppSelector(state => state.enrollment.entity);
  const loading = useAppSelector(state => state.enrollment.loading);
  const updating = useAppSelector(state => state.enrollment.updating);
  const updateSuccess = useAppSelector(state => state.enrollment.updateSuccess);
  const enrollmentStatusValues = Object.keys(EnrollmentStatus);
  const paymentStatusValues = Object.keys(PaymentStatus);

  const handleClose = () => {
    navigate(`/enrollment${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getCourses({}));
    dispatch(getAppUsers({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    values.enrollmentDate = convertDateTimeToServer(values.enrollmentDate);
    if (values.progressPercentage !== undefined && typeof values.progressPercentage !== 'number') {
      values.progressPercentage = Number(values.progressPercentage);
    }
    values.lastAccessedAt = convertDateTimeToServer(values.lastAccessedAt);
    if (values.amountPaid !== undefined && typeof values.amountPaid !== 'number') {
      values.amountPaid = Number(values.amountPaid);
    }
    values.completedAt = convertDateTimeToServer(values.completedAt);

    const entity = {
      ...enrollmentEntity,
      ...values,
      course: courses.find(it => it.id.toString() === values.course?.toString()),
      student: appUsers.find(it => it.id.toString() === values.student?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          enrollmentDate: displayDefaultDateTime(),
          lastAccessedAt: displayDefaultDateTime(),
          completedAt: displayDefaultDateTime(),
        }
      : {
          status: 'ACTIVE',
          paymentStatus: 'PENDING',
          ...enrollmentEntity,
          enrollmentDate: convertDateTimeFromServer(enrollmentEntity.enrollmentDate),
          lastAccessedAt: convertDateTimeFromServer(enrollmentEntity.lastAccessedAt),
          completedAt: convertDateTimeFromServer(enrollmentEntity.completedAt),
          course: enrollmentEntity?.course?.id,
          student: enrollmentEntity?.student?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="edupressApp.enrollment.home.createOrEditLabel" data-cy="EnrollmentCreateUpdateHeading">
            <Translate contentKey="edupressApp.enrollment.home.createOrEditLabel">Create or edit a Enrollment</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="enrollment-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('edupressApp.enrollment.enrollmentDate')}
                id="enrollment-enrollmentDate"
                name="enrollmentDate"
                data-cy="enrollmentDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('edupressApp.enrollment.progressPercentage')}
                id="enrollment-progressPercentage"
                name="progressPercentage"
                data-cy="progressPercentage"
                type="text"
              />
              <ValidatedField
                label={translate('edupressApp.enrollment.progress')}
                id="enrollment-progress"
                name="progress"
                data-cy="progress"
                type="textarea"
              />
              <ValidatedField
                label={translate('edupressApp.enrollment.lastAccessedAt')}
                id="enrollment-lastAccessedAt"
                name="lastAccessedAt"
                data-cy="lastAccessedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('edupressApp.enrollment.status')}
                id="enrollment-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {enrollmentStatusValues.map(enrollmentStatus => (
                  <option value={enrollmentStatus} key={enrollmentStatus}>
                    {translate(`edupressApp.EnrollmentStatus.${enrollmentStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('edupressApp.enrollment.paymentStatus')}
                id="enrollment-paymentStatus"
                name="paymentStatus"
                data-cy="paymentStatus"
                type="select"
              >
                {paymentStatusValues.map(paymentStatus => (
                  <option value={paymentStatus} key={paymentStatus}>
                    {translate(`edupressApp.PaymentStatus.${paymentStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('edupressApp.enrollment.amountPaid')}
                id="enrollment-amountPaid"
                name="amountPaid"
                data-cy="amountPaid"
                type="text"
              />
              <ValidatedField
                label={translate('edupressApp.enrollment.transactionId')}
                id="enrollment-transactionId"
                name="transactionId"
                data-cy="transactionId"
                type="text"
              />
              <ValidatedField
                label={translate('edupressApp.enrollment.completedAt')}
                id="enrollment-completedAt"
                name="completedAt"
                data-cy="completedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                id="enrollment-course"
                name="course"
                data-cy="course"
                label={translate('edupressApp.enrollment.course')}
                type="select"
              >
                <option value="" key="0" />
                {courses
                  ? courses.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="enrollment-student"
                name="student"
                data-cy="student"
                label={translate('edupressApp.enrollment.student')}
                type="select"
              >
                <option value="" key="0" />
                {appUsers
                  ? appUsers.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.email}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/enrollment" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default EnrollmentUpdate;

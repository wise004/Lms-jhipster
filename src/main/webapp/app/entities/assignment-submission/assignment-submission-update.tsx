import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getAssignments } from 'app/entities/assignment/assignment.reducer';
import { getEntities as getAppUsers } from 'app/entities/app-user/app-user.reducer';
import { SubmissionStatus } from 'app/shared/model/enumerations/submission-status.model';
import { createEntity, getEntity, reset, updateEntity } from './assignment-submission.reducer';

export const AssignmentSubmissionUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const assignments = useAppSelector(state => state.assignment.entities);
  const appUsers = useAppSelector(state => state.appUser.entities);
  const assignmentSubmissionEntity = useAppSelector(state => state.assignmentSubmission.entity);
  const loading = useAppSelector(state => state.assignmentSubmission.loading);
  const updating = useAppSelector(state => state.assignmentSubmission.updating);
  const updateSuccess = useAppSelector(state => state.assignmentSubmission.updateSuccess);
  const submissionStatusValues = Object.keys(SubmissionStatus);

  const handleClose = () => {
    navigate(`/assignment-submission${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getAssignments({}));
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
    values.submittedAt = convertDateTimeToServer(values.submittedAt);
    if (values.grade !== undefined && typeof values.grade !== 'number') {
      values.grade = Number(values.grade);
    }

    const entity = {
      ...assignmentSubmissionEntity,
      ...values,
      assignment: assignments.find(it => it.id.toString() === values.assignment?.toString()),
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
          submittedAt: displayDefaultDateTime(),
        }
      : {
          status: 'SUBMITTED',
          ...assignmentSubmissionEntity,
          submittedAt: convertDateTimeFromServer(assignmentSubmissionEntity.submittedAt),
          assignment: assignmentSubmissionEntity?.assignment?.id,
          student: assignmentSubmissionEntity?.student?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="edupressApp.assignmentSubmission.home.createOrEditLabel" data-cy="AssignmentSubmissionCreateUpdateHeading">
            <Translate contentKey="edupressApp.assignmentSubmission.home.createOrEditLabel">
              Create or edit a AssignmentSubmission
            </Translate>
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
                  id="assignment-submission-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('edupressApp.assignmentSubmission.submittedAt')}
                id="assignment-submission-submittedAt"
                name="submittedAt"
                data-cy="submittedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('edupressApp.assignmentSubmission.submissionText')}
                id="assignment-submission-submissionText"
                name="submissionText"
                data-cy="submissionText"
                type="textarea"
              />
              <ValidatedField
                label={translate('edupressApp.assignmentSubmission.fileUrls')}
                id="assignment-submission-fileUrls"
                name="fileUrls"
                data-cy="fileUrls"
                type="textarea"
              />
              <ValidatedField
                label={translate('edupressApp.assignmentSubmission.grade')}
                id="assignment-submission-grade"
                name="grade"
                data-cy="grade"
                type="text"
              />
              <ValidatedField
                label={translate('edupressApp.assignmentSubmission.feedback')}
                id="assignment-submission-feedback"
                name="feedback"
                data-cy="feedback"
                type="textarea"
              />
              <ValidatedField
                label={translate('edupressApp.assignmentSubmission.status')}
                id="assignment-submission-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {submissionStatusValues.map(submissionStatus => (
                  <option value={submissionStatus} key={submissionStatus}>
                    {translate(`edupressApp.SubmissionStatus.${submissionStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                id="assignment-submission-assignment"
                name="assignment"
                data-cy="assignment"
                label={translate('edupressApp.assignmentSubmission.assignment')}
                type="select"
              >
                <option value="" key="0" />
                {assignments
                  ? assignments.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="assignment-submission-student"
                name="student"
                data-cy="student"
                label={translate('edupressApp.assignmentSubmission.student')}
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/assignment-submission" replace color="info">
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

export default AssignmentSubmissionUpdate;

import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getCourses } from 'app/entities/course/course.reducer';
import { getEntities as getLessons } from 'app/entities/lesson/lesson.reducer';
import { createEntity, getEntity, reset, updateEntity } from './assignment.reducer';

export const AssignmentUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const courses = useAppSelector(state => state.course.entities);
  const lessons = useAppSelector(state => state.lesson.entities);
  const assignmentEntity = useAppSelector(state => state.assignment.entity);
  const loading = useAppSelector(state => state.assignment.loading);
  const updating = useAppSelector(state => state.assignment.updating);
  const updateSuccess = useAppSelector(state => state.assignment.updateSuccess);

  const handleClose = () => {
    navigate(`/assignment${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getCourses({}));
    dispatch(getLessons({}));
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
    values.dueDate = convertDateTimeToServer(values.dueDate);
    if (values.maxPoints !== undefined && typeof values.maxPoints !== 'number') {
      values.maxPoints = Number(values.maxPoints);
    }
    if (values.maxFileSize !== undefined && typeof values.maxFileSize !== 'number') {
      values.maxFileSize = Number(values.maxFileSize);
    }
    if (values.lateSubmissionPenalty !== undefined && typeof values.lateSubmissionPenalty !== 'number') {
      values.lateSubmissionPenalty = Number(values.lateSubmissionPenalty);
    }
    if (values.sortOrder !== undefined && typeof values.sortOrder !== 'number') {
      values.sortOrder = Number(values.sortOrder);
    }

    const entity = {
      ...assignmentEntity,
      ...values,
      course: courses.find(it => it.id.toString() === values.course?.toString()),
      lesson: lessons.find(it => it.id.toString() === values.lesson?.toString()),
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
          dueDate: displayDefaultDateTime(),
        }
      : {
          ...assignmentEntity,
          dueDate: convertDateTimeFromServer(assignmentEntity.dueDate),
          course: assignmentEntity?.course?.id,
          lesson: assignmentEntity?.lesson?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="edupressApp.assignment.home.createOrEditLabel" data-cy="AssignmentCreateUpdateHeading">
            <Translate contentKey="edupressApp.assignment.home.createOrEditLabel">Create or edit a Assignment</Translate>
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
                  id="assignment-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('edupressApp.assignment.title')}
                id="assignment-title"
                name="title"
                data-cy="title"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('edupressApp.assignment.description')}
                id="assignment-description"
                name="description"
                data-cy="description"
                type="textarea"
              />
              <ValidatedField
                label={translate('edupressApp.assignment.instructions')}
                id="assignment-instructions"
                name="instructions"
                data-cy="instructions"
                type="textarea"
              />
              <ValidatedField
                label={translate('edupressApp.assignment.dueDate')}
                id="assignment-dueDate"
                name="dueDate"
                data-cy="dueDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('edupressApp.assignment.maxPoints')}
                id="assignment-maxPoints"
                name="maxPoints"
                data-cy="maxPoints"
                type="text"
              />
              <ValidatedField
                label={translate('edupressApp.assignment.submissionType')}
                id="assignment-submissionType"
                name="submissionType"
                data-cy="submissionType"
                type="text"
              />
              <ValidatedField
                label={translate('edupressApp.assignment.allowedFileTypes')}
                id="assignment-allowedFileTypes"
                name="allowedFileTypes"
                data-cy="allowedFileTypes"
                type="textarea"
              />
              <ValidatedField
                label={translate('edupressApp.assignment.maxFileSize')}
                id="assignment-maxFileSize"
                name="maxFileSize"
                data-cy="maxFileSize"
                type="text"
              />
              <ValidatedField
                label={translate('edupressApp.assignment.isPublished')}
                id="assignment-isPublished"
                name="isPublished"
                data-cy="isPublished"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('edupressApp.assignment.allowLateSubmission')}
                id="assignment-allowLateSubmission"
                name="allowLateSubmission"
                data-cy="allowLateSubmission"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('edupressApp.assignment.lateSubmissionPenalty')}
                id="assignment-lateSubmissionPenalty"
                name="lateSubmissionPenalty"
                data-cy="lateSubmissionPenalty"
                type="text"
              />
              <ValidatedField
                label={translate('edupressApp.assignment.sortOrder')}
                id="assignment-sortOrder"
                name="sortOrder"
                data-cy="sortOrder"
                type="text"
              />
              <ValidatedField
                id="assignment-course"
                name="course"
                data-cy="course"
                label={translate('edupressApp.assignment.course')}
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
                id="assignment-lesson"
                name="lesson"
                data-cy="lesson"
                label={translate('edupressApp.assignment.lesson')}
                type="select"
              >
                <option value="" key="0" />
                {lessons
                  ? lessons.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/assignment" replace color="info">
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

export default AssignmentUpdate;

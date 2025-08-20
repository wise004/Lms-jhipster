import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getQuizzes } from 'app/entities/quiz/quiz.reducer';
import { getEntities as getAppUsers } from 'app/entities/app-user/app-user.reducer';
import { AttemptStatus } from 'app/shared/model/enumerations/attempt-status.model';
import { createEntity, getEntity, reset, updateEntity } from './quiz-attempt.reducer';

export const QuizAttemptUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const quizzes = useAppSelector(state => state.quiz.entities);
  const appUsers = useAppSelector(state => state.appUser.entities);
  const quizAttemptEntity = useAppSelector(state => state.quizAttempt.entity);
  const loading = useAppSelector(state => state.quizAttempt.loading);
  const updating = useAppSelector(state => state.quizAttempt.updating);
  const updateSuccess = useAppSelector(state => state.quizAttempt.updateSuccess);
  const attemptStatusValues = Object.keys(AttemptStatus);

  const handleClose = () => {
    navigate(`/quiz-attempt${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getQuizzes({}));
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
    values.startedAt = convertDateTimeToServer(values.startedAt);
    values.submittedAt = convertDateTimeToServer(values.submittedAt);
    if (values.score !== undefined && typeof values.score !== 'number') {
      values.score = Number(values.score);
    }
    if (values.attemptNumber !== undefined && typeof values.attemptNumber !== 'number') {
      values.attemptNumber = Number(values.attemptNumber);
    }

    const entity = {
      ...quizAttemptEntity,
      ...values,
      quiz: quizzes.find(it => it.id.toString() === values.quiz?.toString()),
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
          startedAt: displayDefaultDateTime(),
          submittedAt: displayDefaultDateTime(),
        }
      : {
          status: 'STARTED',
          ...quizAttemptEntity,
          startedAt: convertDateTimeFromServer(quizAttemptEntity.startedAt),
          submittedAt: convertDateTimeFromServer(quizAttemptEntity.submittedAt),
          quiz: quizAttemptEntity?.quiz?.id,
          student: quizAttemptEntity?.student?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="edupressApp.quizAttempt.home.createOrEditLabel" data-cy="QuizAttemptCreateUpdateHeading">
            <Translate contentKey="edupressApp.quizAttempt.home.createOrEditLabel">Create or edit a QuizAttempt</Translate>
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
                  id="quiz-attempt-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('edupressApp.quizAttempt.startedAt')}
                id="quiz-attempt-startedAt"
                name="startedAt"
                data-cy="startedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('edupressApp.quizAttempt.submittedAt')}
                id="quiz-attempt-submittedAt"
                name="submittedAt"
                data-cy="submittedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('edupressApp.quizAttempt.score')}
                id="quiz-attempt-score"
                name="score"
                data-cy="score"
                type="text"
              />
              <ValidatedField
                label={translate('edupressApp.quizAttempt.passed')}
                id="quiz-attempt-passed"
                name="passed"
                data-cy="passed"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('edupressApp.quizAttempt.answers')}
                id="quiz-attempt-answers"
                name="answers"
                data-cy="answers"
                type="textarea"
              />
              <ValidatedField
                label={translate('edupressApp.quizAttempt.attemptNumber')}
                id="quiz-attempt-attemptNumber"
                name="attemptNumber"
                data-cy="attemptNumber"
                type="text"
              />
              <ValidatedField
                label={translate('edupressApp.quizAttempt.status')}
                id="quiz-attempt-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {attemptStatusValues.map(attemptStatus => (
                  <option value={attemptStatus} key={attemptStatus}>
                    {translate(`edupressApp.AttemptStatus.${attemptStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                id="quiz-attempt-quiz"
                name="quiz"
                data-cy="quiz"
                label={translate('edupressApp.quizAttempt.quiz')}
                type="select"
              >
                <option value="" key="0" />
                {quizzes
                  ? quizzes.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="quiz-attempt-student"
                name="student"
                data-cy="student"
                label={translate('edupressApp.quizAttempt.student')}
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/quiz-attempt" replace color="info">
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

export default QuizAttemptUpdate;

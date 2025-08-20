import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getCourses } from 'app/entities/course/course.reducer';
import { getEntities as getLessons } from 'app/entities/lesson/lesson.reducer';
import { createEntity, getEntity, reset, updateEntity } from './quiz.reducer';

export const QuizUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const courses = useAppSelector(state => state.course.entities);
  const lessons = useAppSelector(state => state.lesson.entities);
  const quizEntity = useAppSelector(state => state.quiz.entity);
  const loading = useAppSelector(state => state.quiz.loading);
  const updating = useAppSelector(state => state.quiz.updating);
  const updateSuccess = useAppSelector(state => state.quiz.updateSuccess);

  const handleClose = () => {
    navigate(`/quiz${location.search}`);
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
    if (values.timeLimit !== undefined && typeof values.timeLimit !== 'number') {
      values.timeLimit = Number(values.timeLimit);
    }
    if (values.passingScore !== undefined && typeof values.passingScore !== 'number') {
      values.passingScore = Number(values.passingScore);
    }
    if (values.attemptsAllowed !== undefined && typeof values.attemptsAllowed !== 'number') {
      values.attemptsAllowed = Number(values.attemptsAllowed);
    }
    if (values.sortOrder !== undefined && typeof values.sortOrder !== 'number') {
      values.sortOrder = Number(values.sortOrder);
    }
    values.availableFrom = convertDateTimeToServer(values.availableFrom);
    values.availableUntil = convertDateTimeToServer(values.availableUntil);

    const entity = {
      ...quizEntity,
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
          availableFrom: displayDefaultDateTime(),
          availableUntil: displayDefaultDateTime(),
        }
      : {
          ...quizEntity,
          availableFrom: convertDateTimeFromServer(quizEntity.availableFrom),
          availableUntil: convertDateTimeFromServer(quizEntity.availableUntil),
          course: quizEntity?.course?.id,
          lesson: quizEntity?.lesson?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="edupressApp.quiz.home.createOrEditLabel" data-cy="QuizCreateUpdateHeading">
            <Translate contentKey="edupressApp.quiz.home.createOrEditLabel">Create or edit a Quiz</Translate>
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
                  id="quiz-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('edupressApp.quiz.title')}
                id="quiz-title"
                name="title"
                data-cy="title"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('edupressApp.quiz.description')}
                id="quiz-description"
                name="description"
                data-cy="description"
                type="textarea"
              />
              <ValidatedField
                label={translate('edupressApp.quiz.timeLimit')}
                id="quiz-timeLimit"
                name="timeLimit"
                data-cy="timeLimit"
                type="text"
              />
              <ValidatedField
                label={translate('edupressApp.quiz.passingScore')}
                id="quiz-passingScore"
                name="passingScore"
                data-cy="passingScore"
                type="text"
              />
              <ValidatedField
                label={translate('edupressApp.quiz.attemptsAllowed')}
                id="quiz-attemptsAllowed"
                name="attemptsAllowed"
                data-cy="attemptsAllowed"
                type="text"
              />
              <ValidatedField
                label={translate('edupressApp.quiz.sortOrder')}
                id="quiz-sortOrder"
                name="sortOrder"
                data-cy="sortOrder"
                type="text"
              />
              <ValidatedField
                label={translate('edupressApp.quiz.questions')}
                id="quiz-questions"
                name="questions"
                data-cy="questions"
                type="textarea"
              />
              <ValidatedField
                label={translate('edupressApp.quiz.isPublished')}
                id="quiz-isPublished"
                name="isPublished"
                data-cy="isPublished"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('edupressApp.quiz.availableFrom')}
                id="quiz-availableFrom"
                name="availableFrom"
                data-cy="availableFrom"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('edupressApp.quiz.availableUntil')}
                id="quiz-availableUntil"
                name="availableUntil"
                data-cy="availableUntil"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField id="quiz-course" name="course" data-cy="course" label={translate('edupressApp.quiz.course')} type="select">
                <option value="" key="0" />
                {courses
                  ? courses.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField id="quiz-lesson" name="lesson" data-cy="lesson" label={translate('edupressApp.quiz.lesson')} type="select">
                <option value="" key="0" />
                {lessons
                  ? lessons.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/quiz" replace color="info">
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

export default QuizUpdate;

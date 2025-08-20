import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './quiz.reducer';

export const QuizDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const quizEntity = useAppSelector(state => state.quiz.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="quizDetailsHeading">
          <Translate contentKey="edupressApp.quiz.detail.title">Quiz</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{quizEntity.id}</dd>
          <dt>
            <span id="title">
              <Translate contentKey="edupressApp.quiz.title">Title</Translate>
            </span>
          </dt>
          <dd>{quizEntity.title}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="edupressApp.quiz.description">Description</Translate>
            </span>
          </dt>
          <dd>{quizEntity.description}</dd>
          <dt>
            <span id="timeLimit">
              <Translate contentKey="edupressApp.quiz.timeLimit">Time Limit</Translate>
            </span>
          </dt>
          <dd>{quizEntity.timeLimit}</dd>
          <dt>
            <span id="passingScore">
              <Translate contentKey="edupressApp.quiz.passingScore">Passing Score</Translate>
            </span>
          </dt>
          <dd>{quizEntity.passingScore}</dd>
          <dt>
            <span id="attemptsAllowed">
              <Translate contentKey="edupressApp.quiz.attemptsAllowed">Attempts Allowed</Translate>
            </span>
          </dt>
          <dd>{quizEntity.attemptsAllowed}</dd>
          <dt>
            <span id="sortOrder">
              <Translate contentKey="edupressApp.quiz.sortOrder">Sort Order</Translate>
            </span>
          </dt>
          <dd>{quizEntity.sortOrder}</dd>
          <dt>
            <span id="questions">
              <Translate contentKey="edupressApp.quiz.questions">Questions</Translate>
            </span>
          </dt>
          <dd>{quizEntity.questions}</dd>
          <dt>
            <span id="isPublished">
              <Translate contentKey="edupressApp.quiz.isPublished">Is Published</Translate>
            </span>
          </dt>
          <dd>{quizEntity.isPublished ? 'true' : 'false'}</dd>
          <dt>
            <span id="availableFrom">
              <Translate contentKey="edupressApp.quiz.availableFrom">Available From</Translate>
            </span>
          </dt>
          <dd>{quizEntity.availableFrom ? <TextFormat value={quizEntity.availableFrom} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="availableUntil">
              <Translate contentKey="edupressApp.quiz.availableUntil">Available Until</Translate>
            </span>
          </dt>
          <dd>
            {quizEntity.availableUntil ? <TextFormat value={quizEntity.availableUntil} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <Translate contentKey="edupressApp.quiz.course">Course</Translate>
          </dt>
          <dd>{quizEntity.course ? quizEntity.course.id : ''}</dd>
          <dt>
            <Translate contentKey="edupressApp.quiz.lesson">Lesson</Translate>
          </dt>
          <dd>{quizEntity.lesson ? quizEntity.lesson.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/quiz" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/quiz/${quizEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default QuizDetail;

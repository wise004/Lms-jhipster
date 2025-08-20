import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './quiz-attempt.reducer';

export const QuizAttemptDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const quizAttemptEntity = useAppSelector(state => state.quizAttempt.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="quizAttemptDetailsHeading">
          <Translate contentKey="edupressApp.quizAttempt.detail.title">QuizAttempt</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{quizAttemptEntity.id}</dd>
          <dt>
            <span id="startedAt">
              <Translate contentKey="edupressApp.quizAttempt.startedAt">Started At</Translate>
            </span>
          </dt>
          <dd>
            {quizAttemptEntity.startedAt ? <TextFormat value={quizAttemptEntity.startedAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="submittedAt">
              <Translate contentKey="edupressApp.quizAttempt.submittedAt">Submitted At</Translate>
            </span>
          </dt>
          <dd>
            {quizAttemptEntity.submittedAt ? (
              <TextFormat value={quizAttemptEntity.submittedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="score">
              <Translate contentKey="edupressApp.quizAttempt.score">Score</Translate>
            </span>
          </dt>
          <dd>{quizAttemptEntity.score}</dd>
          <dt>
            <span id="passed">
              <Translate contentKey="edupressApp.quizAttempt.passed">Passed</Translate>
            </span>
          </dt>
          <dd>{quizAttemptEntity.passed ? 'true' : 'false'}</dd>
          <dt>
            <span id="answers">
              <Translate contentKey="edupressApp.quizAttempt.answers">Answers</Translate>
            </span>
          </dt>
          <dd>{quizAttemptEntity.answers}</dd>
          <dt>
            <span id="attemptNumber">
              <Translate contentKey="edupressApp.quizAttempt.attemptNumber">Attempt Number</Translate>
            </span>
          </dt>
          <dd>{quizAttemptEntity.attemptNumber}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="edupressApp.quizAttempt.status">Status</Translate>
            </span>
          </dt>
          <dd>{quizAttemptEntity.status}</dd>
          <dt>
            <Translate contentKey="edupressApp.quizAttempt.quiz">Quiz</Translate>
          </dt>
          <dd>{quizAttemptEntity.quiz ? quizAttemptEntity.quiz.id : ''}</dd>
          <dt>
            <Translate contentKey="edupressApp.quizAttempt.student">Student</Translate>
          </dt>
          <dd>{quizAttemptEntity.student ? quizAttemptEntity.student.email : ''}</dd>
        </dl>
        <Button tag={Link} to="/quiz-attempt" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/quiz-attempt/${quizAttemptEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default QuizAttemptDetail;

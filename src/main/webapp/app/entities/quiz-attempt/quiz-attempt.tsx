import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, TextFormat, Translate, getPaginationState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { APP_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './quiz-attempt.reducer';

export const QuizAttempt = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const quizAttemptList = useAppSelector(state => state.quizAttempt.entities);
  const loading = useAppSelector(state => state.quizAttempt.loading);
  const totalItems = useAppSelector(state => state.quizAttempt.totalItems);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [paginationState.activePage, paginationState.order, paginationState.sort]);

  useEffect(() => {
    const params = new URLSearchParams(pageLocation.search);
    const page = params.get('page');
    const sort = params.get(SORT);
    if (page && sort) {
      const sortSplit = sort.split(',');
      setPaginationState({
        ...paginationState,
        activePage: +page,
        sort: sortSplit[0],
        order: sortSplit[1],
      });
    }
  }, [pageLocation.search]);

  const sort = p => () => {
    setPaginationState({
      ...paginationState,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handlePagination = currentPage =>
    setPaginationState({
      ...paginationState,
      activePage: currentPage,
    });

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = paginationState.sort;
    const order = paginationState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="quiz-attempt-heading" data-cy="QuizAttemptHeading">
        <Translate contentKey="edupressApp.quizAttempt.home.title">Quiz Attempts</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="edupressApp.quizAttempt.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/quiz-attempt/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="edupressApp.quizAttempt.home.createLabel">Create new Quiz Attempt</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {quizAttemptList && quizAttemptList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="edupressApp.quizAttempt.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('startedAt')}>
                  <Translate contentKey="edupressApp.quizAttempt.startedAt">Started At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('startedAt')} />
                </th>
                <th className="hand" onClick={sort('submittedAt')}>
                  <Translate contentKey="edupressApp.quizAttempt.submittedAt">Submitted At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('submittedAt')} />
                </th>
                <th className="hand" onClick={sort('score')}>
                  <Translate contentKey="edupressApp.quizAttempt.score">Score</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('score')} />
                </th>
                <th className="hand" onClick={sort('passed')}>
                  <Translate contentKey="edupressApp.quizAttempt.passed">Passed</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('passed')} />
                </th>
                <th className="hand" onClick={sort('answers')}>
                  <Translate contentKey="edupressApp.quizAttempt.answers">Answers</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('answers')} />
                </th>
                <th className="hand" onClick={sort('attemptNumber')}>
                  <Translate contentKey="edupressApp.quizAttempt.attemptNumber">Attempt Number</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('attemptNumber')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="edupressApp.quizAttempt.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th>
                  <Translate contentKey="edupressApp.quizAttempt.quiz">Quiz</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="edupressApp.quizAttempt.student">Student</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {quizAttemptList.map((quizAttempt, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/quiz-attempt/${quizAttempt.id}`} color="link" size="sm">
                      {quizAttempt.id}
                    </Button>
                  </td>
                  <td>
                    {quizAttempt.startedAt ? <TextFormat type="date" value={quizAttempt.startedAt} format={APP_DATE_FORMAT} /> : null}
                  </td>
                  <td>
                    {quizAttempt.submittedAt ? <TextFormat type="date" value={quizAttempt.submittedAt} format={APP_DATE_FORMAT} /> : null}
                  </td>
                  <td>{quizAttempt.score}</td>
                  <td>{quizAttempt.passed ? 'true' : 'false'}</td>
                  <td>{quizAttempt.answers}</td>
                  <td>{quizAttempt.attemptNumber}</td>
                  <td>
                    <Translate contentKey={`edupressApp.AttemptStatus.${quizAttempt.status}`} />
                  </td>
                  <td>{quizAttempt.quiz ? <Link to={`/quiz/${quizAttempt.quiz.id}`}>{quizAttempt.quiz.id}</Link> : ''}</td>
                  <td>{quizAttempt.student ? <Link to={`/app-user/${quizAttempt.student.id}`}>{quizAttempt.student.email}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/quiz-attempt/${quizAttempt.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/quiz-attempt/${quizAttempt.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                        color="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        onClick={() =>
                          (window.location.href = `/quiz-attempt/${quizAttempt.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
                        }
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="edupressApp.quizAttempt.home.notFound">No Quiz Attempts found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={quizAttemptList && quizAttemptList.length > 0 ? '' : 'd-none'}>
          <div className="justify-content-center d-flex">
            <JhiItemCount page={paginationState.activePage} total={totalItems} itemsPerPage={paginationState.itemsPerPage} i18nEnabled />
          </div>
          <div className="justify-content-center d-flex">
            <JhiPagination
              activePage={paginationState.activePage}
              onSelect={handlePagination}
              maxButtons={5}
              itemsPerPage={paginationState.itemsPerPage}
              totalItems={totalItems}
            />
          </div>
        </div>
      ) : (
        ''
      )}
    </div>
  );
};

export default QuizAttempt;

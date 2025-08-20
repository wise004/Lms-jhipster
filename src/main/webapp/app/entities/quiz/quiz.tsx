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

import { getEntities } from './quiz.reducer';

export const Quiz = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const quizList = useAppSelector(state => state.quiz.entities);
  const loading = useAppSelector(state => state.quiz.loading);
  const totalItems = useAppSelector(state => state.quiz.totalItems);

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
      <h2 id="quiz-heading" data-cy="QuizHeading">
        <Translate contentKey="edupressApp.quiz.home.title">Quizzes</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="edupressApp.quiz.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/quiz/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="edupressApp.quiz.home.createLabel">Create new Quiz</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {quizList && quizList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="edupressApp.quiz.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('title')}>
                  <Translate contentKey="edupressApp.quiz.title">Title</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('title')} />
                </th>
                <th className="hand" onClick={sort('description')}>
                  <Translate contentKey="edupressApp.quiz.description">Description</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('description')} />
                </th>
                <th className="hand" onClick={sort('timeLimit')}>
                  <Translate contentKey="edupressApp.quiz.timeLimit">Time Limit</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('timeLimit')} />
                </th>
                <th className="hand" onClick={sort('passingScore')}>
                  <Translate contentKey="edupressApp.quiz.passingScore">Passing Score</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('passingScore')} />
                </th>
                <th className="hand" onClick={sort('attemptsAllowed')}>
                  <Translate contentKey="edupressApp.quiz.attemptsAllowed">Attempts Allowed</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('attemptsAllowed')} />
                </th>
                <th className="hand" onClick={sort('sortOrder')}>
                  <Translate contentKey="edupressApp.quiz.sortOrder">Sort Order</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('sortOrder')} />
                </th>
                <th className="hand" onClick={sort('questions')}>
                  <Translate contentKey="edupressApp.quiz.questions">Questions</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('questions')} />
                </th>
                <th className="hand" onClick={sort('isPublished')}>
                  <Translate contentKey="edupressApp.quiz.isPublished">Is Published</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('isPublished')} />
                </th>
                <th className="hand" onClick={sort('availableFrom')}>
                  <Translate contentKey="edupressApp.quiz.availableFrom">Available From</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('availableFrom')} />
                </th>
                <th className="hand" onClick={sort('availableUntil')}>
                  <Translate contentKey="edupressApp.quiz.availableUntil">Available Until</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('availableUntil')} />
                </th>
                <th>
                  <Translate contentKey="edupressApp.quiz.course">Course</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="edupressApp.quiz.lesson">Lesson</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {quizList.map((quiz, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/quiz/${quiz.id}`} color="link" size="sm">
                      {quiz.id}
                    </Button>
                  </td>
                  <td>{quiz.title}</td>
                  <td>{quiz.description}</td>
                  <td>{quiz.timeLimit}</td>
                  <td>{quiz.passingScore}</td>
                  <td>{quiz.attemptsAllowed}</td>
                  <td>{quiz.sortOrder}</td>
                  <td>{quiz.questions}</td>
                  <td>{quiz.isPublished ? 'true' : 'false'}</td>
                  <td>{quiz.availableFrom ? <TextFormat type="date" value={quiz.availableFrom} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{quiz.availableUntil ? <TextFormat type="date" value={quiz.availableUntil} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{quiz.course ? <Link to={`/course/${quiz.course.id}`}>{quiz.course.id}</Link> : ''}</td>
                  <td>{quiz.lesson ? <Link to={`/lesson/${quiz.lesson.id}`}>{quiz.lesson.id}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/quiz/${quiz.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/quiz/${quiz.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (window.location.href = `/quiz/${quiz.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="edupressApp.quiz.home.notFound">No Quizzes found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={quizList && quizList.length > 0 ? '' : 'd-none'}>
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

export default Quiz;

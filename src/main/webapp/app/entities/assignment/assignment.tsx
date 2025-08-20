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

import { getEntities } from './assignment.reducer';

export const Assignment = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const assignmentList = useAppSelector(state => state.assignment.entities);
  const loading = useAppSelector(state => state.assignment.loading);
  const totalItems = useAppSelector(state => state.assignment.totalItems);

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
      <h2 id="assignment-heading" data-cy="AssignmentHeading">
        <Translate contentKey="edupressApp.assignment.home.title">Assignments</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="edupressApp.assignment.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/assignment/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="edupressApp.assignment.home.createLabel">Create new Assignment</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {assignmentList && assignmentList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="edupressApp.assignment.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('title')}>
                  <Translate contentKey="edupressApp.assignment.title">Title</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('title')} />
                </th>
                <th className="hand" onClick={sort('description')}>
                  <Translate contentKey="edupressApp.assignment.description">Description</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('description')} />
                </th>
                <th className="hand" onClick={sort('instructions')}>
                  <Translate contentKey="edupressApp.assignment.instructions">Instructions</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('instructions')} />
                </th>
                <th className="hand" onClick={sort('dueDate')}>
                  <Translate contentKey="edupressApp.assignment.dueDate">Due Date</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('dueDate')} />
                </th>
                <th className="hand" onClick={sort('maxPoints')}>
                  <Translate contentKey="edupressApp.assignment.maxPoints">Max Points</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('maxPoints')} />
                </th>
                <th className="hand" onClick={sort('submissionType')}>
                  <Translate contentKey="edupressApp.assignment.submissionType">Submission Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('submissionType')} />
                </th>
                <th className="hand" onClick={sort('allowedFileTypes')}>
                  <Translate contentKey="edupressApp.assignment.allowedFileTypes">Allowed File Types</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('allowedFileTypes')} />
                </th>
                <th className="hand" onClick={sort('maxFileSize')}>
                  <Translate contentKey="edupressApp.assignment.maxFileSize">Max File Size</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('maxFileSize')} />
                </th>
                <th className="hand" onClick={sort('isPublished')}>
                  <Translate contentKey="edupressApp.assignment.isPublished">Is Published</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('isPublished')} />
                </th>
                <th className="hand" onClick={sort('allowLateSubmission')}>
                  <Translate contentKey="edupressApp.assignment.allowLateSubmission">Allow Late Submission</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('allowLateSubmission')} />
                </th>
                <th className="hand" onClick={sort('lateSubmissionPenalty')}>
                  <Translate contentKey="edupressApp.assignment.lateSubmissionPenalty">Late Submission Penalty</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('lateSubmissionPenalty')} />
                </th>
                <th className="hand" onClick={sort('sortOrder')}>
                  <Translate contentKey="edupressApp.assignment.sortOrder">Sort Order</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('sortOrder')} />
                </th>
                <th>
                  <Translate contentKey="edupressApp.assignment.course">Course</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="edupressApp.assignment.lesson">Lesson</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {assignmentList.map((assignment, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/assignment/${assignment.id}`} color="link" size="sm">
                      {assignment.id}
                    </Button>
                  </td>
                  <td>{assignment.title}</td>
                  <td>{assignment.description}</td>
                  <td>{assignment.instructions}</td>
                  <td>{assignment.dueDate ? <TextFormat type="date" value={assignment.dueDate} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{assignment.maxPoints}</td>
                  <td>{assignment.submissionType}</td>
                  <td>{assignment.allowedFileTypes}</td>
                  <td>{assignment.maxFileSize}</td>
                  <td>{assignment.isPublished ? 'true' : 'false'}</td>
                  <td>{assignment.allowLateSubmission ? 'true' : 'false'}</td>
                  <td>{assignment.lateSubmissionPenalty}</td>
                  <td>{assignment.sortOrder}</td>
                  <td>{assignment.course ? <Link to={`/course/${assignment.course.id}`}>{assignment.course.id}</Link> : ''}</td>
                  <td>{assignment.lesson ? <Link to={`/lesson/${assignment.lesson.id}`}>{assignment.lesson.id}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/assignment/${assignment.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/assignment/${assignment.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (window.location.href = `/assignment/${assignment.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="edupressApp.assignment.home.notFound">No Assignments found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={assignmentList && assignmentList.length > 0 ? '' : 'd-none'}>
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

export default Assignment;

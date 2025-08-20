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

import { getEntities } from './assignment-submission.reducer';

export const AssignmentSubmission = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const assignmentSubmissionList = useAppSelector(state => state.assignmentSubmission.entities);
  const loading = useAppSelector(state => state.assignmentSubmission.loading);
  const totalItems = useAppSelector(state => state.assignmentSubmission.totalItems);

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
      <h2 id="assignment-submission-heading" data-cy="AssignmentSubmissionHeading">
        <Translate contentKey="edupressApp.assignmentSubmission.home.title">Assignment Submissions</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="edupressApp.assignmentSubmission.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link
            to="/assignment-submission/new"
            className="btn btn-primary jh-create-entity"
            id="jh-create-entity"
            data-cy="entityCreateButton"
          >
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="edupressApp.assignmentSubmission.home.createLabel">Create new Assignment Submission</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {assignmentSubmissionList && assignmentSubmissionList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="edupressApp.assignmentSubmission.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('submittedAt')}>
                  <Translate contentKey="edupressApp.assignmentSubmission.submittedAt">Submitted At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('submittedAt')} />
                </th>
                <th className="hand" onClick={sort('submissionText')}>
                  <Translate contentKey="edupressApp.assignmentSubmission.submissionText">Submission Text</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('submissionText')} />
                </th>
                <th className="hand" onClick={sort('fileUrls')}>
                  <Translate contentKey="edupressApp.assignmentSubmission.fileUrls">File Urls</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('fileUrls')} />
                </th>
                <th className="hand" onClick={sort('grade')}>
                  <Translate contentKey="edupressApp.assignmentSubmission.grade">Grade</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('grade')} />
                </th>
                <th className="hand" onClick={sort('feedback')}>
                  <Translate contentKey="edupressApp.assignmentSubmission.feedback">Feedback</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('feedback')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="edupressApp.assignmentSubmission.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th>
                  <Translate contentKey="edupressApp.assignmentSubmission.assignment">Assignment</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="edupressApp.assignmentSubmission.student">Student</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {assignmentSubmissionList.map((assignmentSubmission, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/assignment-submission/${assignmentSubmission.id}`} color="link" size="sm">
                      {assignmentSubmission.id}
                    </Button>
                  </td>
                  <td>
                    {assignmentSubmission.submittedAt ? (
                      <TextFormat type="date" value={assignmentSubmission.submittedAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{assignmentSubmission.submissionText}</td>
                  <td>{assignmentSubmission.fileUrls}</td>
                  <td>{assignmentSubmission.grade}</td>
                  <td>{assignmentSubmission.feedback}</td>
                  <td>
                    <Translate contentKey={`edupressApp.SubmissionStatus.${assignmentSubmission.status}`} />
                  </td>
                  <td>
                    {assignmentSubmission.assignment ? (
                      <Link to={`/assignment/${assignmentSubmission.assignment.id}`}>{assignmentSubmission.assignment.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>
                    {assignmentSubmission.student ? (
                      <Link to={`/app-user/${assignmentSubmission.student.id}`}>{assignmentSubmission.student.email}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        tag={Link}
                        to={`/assignment-submission/${assignmentSubmission.id}`}
                        color="info"
                        size="sm"
                        data-cy="entityDetailsButton"
                      >
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/assignment-submission/${assignmentSubmission.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (window.location.href = `/assignment-submission/${assignmentSubmission.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="edupressApp.assignmentSubmission.home.notFound">No Assignment Submissions found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={assignmentSubmissionList && assignmentSubmissionList.length > 0 ? '' : 'd-none'}>
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

export default AssignmentSubmission;

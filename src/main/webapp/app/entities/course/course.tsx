import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, Translate, getPaginationState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './course.reducer';

export const Course = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const courseList = useAppSelector(state => state.course.entities);
  const loading = useAppSelector(state => state.course.loading);
  const totalItems = useAppSelector(state => state.course.totalItems);

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
      <h2 id="course-heading" data-cy="CourseHeading">
        <Translate contentKey="edupressApp.course.home.title">Courses</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="edupressApp.course.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/course/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="edupressApp.course.home.createLabel">Create new Course</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {courseList && courseList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="edupressApp.course.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('title')}>
                  <Translate contentKey="edupressApp.course.title">Title</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('title')} />
                </th>
                <th className="hand" onClick={sort('slug')}>
                  <Translate contentKey="edupressApp.course.slug">Slug</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('slug')} />
                </th>
                <th className="hand" onClick={sort('shortDescription')}>
                  <Translate contentKey="edupressApp.course.shortDescription">Short Description</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('shortDescription')} />
                </th>
                <th className="hand" onClick={sort('description')}>
                  <Translate contentKey="edupressApp.course.description">Description</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('description')} />
                </th>
                <th className="hand" onClick={sort('thumbnailUrl')}>
                  <Translate contentKey="edupressApp.course.thumbnailUrl">Thumbnail Url</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('thumbnailUrl')} />
                </th>
                <th className="hand" onClick={sort('price')}>
                  <Translate contentKey="edupressApp.course.price">Price</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('price')} />
                </th>
                <th className="hand" onClick={sort('originalPrice')}>
                  <Translate contentKey="edupressApp.course.originalPrice">Original Price</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('originalPrice')} />
                </th>
                <th className="hand" onClick={sort('level')}>
                  <Translate contentKey="edupressApp.course.level">Level</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('level')} />
                </th>
                <th className="hand" onClick={sort('language')}>
                  <Translate contentKey="edupressApp.course.language">Language</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('language')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="edupressApp.course.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th className="hand" onClick={sort('isPublished')}>
                  <Translate contentKey="edupressApp.course.isPublished">Is Published</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('isPublished')} />
                </th>
                <th className="hand" onClick={sort('isFeatured')}>
                  <Translate contentKey="edupressApp.course.isFeatured">Is Featured</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('isFeatured')} />
                </th>
                <th className="hand" onClick={sort('averageRating')}>
                  <Translate contentKey="edupressApp.course.averageRating">Average Rating</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('averageRating')} />
                </th>
                <th className="hand" onClick={sort('enrollmentCount')}>
                  <Translate contentKey="edupressApp.course.enrollmentCount">Enrollment Count</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('enrollmentCount')} />
                </th>
                <th>
                  <Translate contentKey="edupressApp.course.instructor">Instructor</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="edupressApp.course.category">Category</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {courseList.map((course, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/course/${course.id}`} color="link" size="sm">
                      {course.id}
                    </Button>
                  </td>
                  <td>{course.title}</td>
                  <td>{course.slug}</td>
                  <td>{course.shortDescription}</td>
                  <td>{course.description}</td>
                  <td>{course.thumbnailUrl}</td>
                  <td>{course.price}</td>
                  <td>{course.originalPrice}</td>
                  <td>{course.level}</td>
                  <td>{course.language}</td>
                  <td>
                    <Translate contentKey={`edupressApp.CourseStatus.${course.status}`} />
                  </td>
                  <td>{course.isPublished ? 'true' : 'false'}</td>
                  <td>{course.isFeatured ? 'true' : 'false'}</td>
                  <td>{course.averageRating}</td>
                  <td>{course.enrollmentCount}</td>
                  <td>{course.instructor ? <Link to={`/app-user/${course.instructor.id}`}>{course.instructor.email}</Link> : ''}</td>
                  <td>{course.category ? <Link to={`/category/${course.category.id}`}>{course.category.name}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/course/${course.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/course/${course.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (window.location.href = `/course/${course.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="edupressApp.course.home.notFound">No Courses found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={courseList && courseList.length > 0 ? '' : 'd-none'}>
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

export default Course;

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

import { getEntities } from './blog-post.reducer';

export const BlogPost = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const blogPostList = useAppSelector(state => state.blogPost.entities);
  const loading = useAppSelector(state => state.blogPost.loading);
  const totalItems = useAppSelector(state => state.blogPost.totalItems);

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
      <h2 id="blog-post-heading" data-cy="BlogPostHeading">
        <Translate contentKey="edupressApp.blogPost.home.title">Blog Posts</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="edupressApp.blogPost.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/blog-post/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="edupressApp.blogPost.home.createLabel">Create new Blog Post</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {blogPostList && blogPostList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="edupressApp.blogPost.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('title')}>
                  <Translate contentKey="edupressApp.blogPost.title">Title</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('title')} />
                </th>
                <th className="hand" onClick={sort('slug')}>
                  <Translate contentKey="edupressApp.blogPost.slug">Slug</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('slug')} />
                </th>
                <th className="hand" onClick={sort('summary')}>
                  <Translate contentKey="edupressApp.blogPost.summary">Summary</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('summary')} />
                </th>
                <th className="hand" onClick={sort('content')}>
                  <Translate contentKey="edupressApp.blogPost.content">Content</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('content')} />
                </th>
                <th className="hand" onClick={sort('coverImageUrl')}>
                  <Translate contentKey="edupressApp.blogPost.coverImageUrl">Cover Image Url</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('coverImageUrl')} />
                </th>
                <th className="hand" onClick={sort('publishDate')}>
                  <Translate contentKey="edupressApp.blogPost.publishDate">Publish Date</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('publishDate')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="edupressApp.blogPost.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th>
                  <Translate contentKey="edupressApp.blogPost.author">Author</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {blogPostList.map((blogPost, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/blog-post/${blogPost.id}`} color="link" size="sm">
                      {blogPost.id}
                    </Button>
                  </td>
                  <td>{blogPost.title}</td>
                  <td>{blogPost.slug}</td>
                  <td>{blogPost.summary}</td>
                  <td>{blogPost.content}</td>
                  <td>{blogPost.coverImageUrl}</td>
                  <td>{blogPost.publishDate ? <TextFormat type="date" value={blogPost.publishDate} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>
                    <Translate contentKey={`edupressApp.PostStatus.${blogPost.status}`} />
                  </td>
                  <td>{blogPost.author ? <Link to={`/app-user/${blogPost.author.id}`}>{blogPost.author.email}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/blog-post/${blogPost.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/blog-post/${blogPost.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (window.location.href = `/blog-post/${blogPost.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="edupressApp.blogPost.home.notFound">No Blog Posts found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={blogPostList && blogPostList.length > 0 ? '' : 'd-none'}>
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

export default BlogPost;

import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './blog-post.reducer';

export const BlogPostDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const blogPostEntity = useAppSelector(state => state.blogPost.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="blogPostDetailsHeading">
          <Translate contentKey="edupressApp.blogPost.detail.title">BlogPost</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{blogPostEntity.id}</dd>
          <dt>
            <span id="title">
              <Translate contentKey="edupressApp.blogPost.title">Title</Translate>
            </span>
          </dt>
          <dd>{blogPostEntity.title}</dd>
          <dt>
            <span id="slug">
              <Translate contentKey="edupressApp.blogPost.slug">Slug</Translate>
            </span>
          </dt>
          <dd>{blogPostEntity.slug}</dd>
          <dt>
            <span id="summary">
              <Translate contentKey="edupressApp.blogPost.summary">Summary</Translate>
            </span>
          </dt>
          <dd>{blogPostEntity.summary}</dd>
          <dt>
            <span id="content">
              <Translate contentKey="edupressApp.blogPost.content">Content</Translate>
            </span>
          </dt>
          <dd>{blogPostEntity.content}</dd>
          <dt>
            <span id="coverImageUrl">
              <Translate contentKey="edupressApp.blogPost.coverImageUrl">Cover Image Url</Translate>
            </span>
          </dt>
          <dd>{blogPostEntity.coverImageUrl}</dd>
          <dt>
            <span id="publishDate">
              <Translate contentKey="edupressApp.blogPost.publishDate">Publish Date</Translate>
            </span>
          </dt>
          <dd>
            {blogPostEntity.publishDate ? <TextFormat value={blogPostEntity.publishDate} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="status">
              <Translate contentKey="edupressApp.blogPost.status">Status</Translate>
            </span>
          </dt>
          <dd>{blogPostEntity.status}</dd>
          <dt>
            <Translate contentKey="edupressApp.blogPost.author">Author</Translate>
          </dt>
          <dd>{blogPostEntity.author ? blogPostEntity.author.email : ''}</dd>
        </dl>
        <Button tag={Link} to="/blog-post" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/blog-post/${blogPostEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default BlogPostDetail;

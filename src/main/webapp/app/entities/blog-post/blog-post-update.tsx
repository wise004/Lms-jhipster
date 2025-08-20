import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getAppUsers } from 'app/entities/app-user/app-user.reducer';
import { PostStatus } from 'app/shared/model/enumerations/post-status.model';
import { createEntity, getEntity, reset, updateEntity } from './blog-post.reducer';

export const BlogPostUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const appUsers = useAppSelector(state => state.appUser.entities);
  const blogPostEntity = useAppSelector(state => state.blogPost.entity);
  const loading = useAppSelector(state => state.blogPost.loading);
  const updating = useAppSelector(state => state.blogPost.updating);
  const updateSuccess = useAppSelector(state => state.blogPost.updateSuccess);
  const postStatusValues = Object.keys(PostStatus);

  const handleClose = () => {
    navigate(`/blog-post${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getAppUsers({}));
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
    values.publishDate = convertDateTimeToServer(values.publishDate);

    const entity = {
      ...blogPostEntity,
      ...values,
      author: appUsers.find(it => it.id.toString() === values.author?.toString()),
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
          publishDate: displayDefaultDateTime(),
        }
      : {
          status: 'DRAFT',
          ...blogPostEntity,
          publishDate: convertDateTimeFromServer(blogPostEntity.publishDate),
          author: blogPostEntity?.author?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="edupressApp.blogPost.home.createOrEditLabel" data-cy="BlogPostCreateUpdateHeading">
            <Translate contentKey="edupressApp.blogPost.home.createOrEditLabel">Create or edit a BlogPost</Translate>
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
                  id="blog-post-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('edupressApp.blogPost.title')}
                id="blog-post-title"
                name="title"
                data-cy="title"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('edupressApp.blogPost.slug')}
                id="blog-post-slug"
                name="slug"
                data-cy="slug"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('edupressApp.blogPost.summary')}
                id="blog-post-summary"
                name="summary"
                data-cy="summary"
                type="text"
              />
              <ValidatedField
                label={translate('edupressApp.blogPost.content')}
                id="blog-post-content"
                name="content"
                data-cy="content"
                type="textarea"
              />
              <ValidatedField
                label={translate('edupressApp.blogPost.coverImageUrl')}
                id="blog-post-coverImageUrl"
                name="coverImageUrl"
                data-cy="coverImageUrl"
                type="text"
              />
              <ValidatedField
                label={translate('edupressApp.blogPost.publishDate')}
                id="blog-post-publishDate"
                name="publishDate"
                data-cy="publishDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('edupressApp.blogPost.status')}
                id="blog-post-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {postStatusValues.map(postStatus => (
                  <option value={postStatus} key={postStatus}>
                    {translate(`edupressApp.PostStatus.${postStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                id="blog-post-author"
                name="author"
                data-cy="author"
                label={translate('edupressApp.blogPost.author')}
                type="select"
              >
                <option value="" key="0" />
                {appUsers
                  ? appUsers.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.email}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/blog-post" replace color="info">
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

export default BlogPostUpdate;

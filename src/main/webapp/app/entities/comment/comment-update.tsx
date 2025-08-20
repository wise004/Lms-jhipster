import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getLessons } from 'app/entities/lesson/lesson.reducer';
import { getEntities as getAppUsers } from 'app/entities/app-user/app-user.reducer';
import { getEntities as getComments } from 'app/entities/comment/comment.reducer';
import { CommentStatus } from 'app/shared/model/enumerations/comment-status.model';
import { createEntity, getEntity, reset, updateEntity } from './comment.reducer';

export const CommentUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const lessons = useAppSelector(state => state.lesson.entities);
  const appUsers = useAppSelector(state => state.appUser.entities);
  const comments = useAppSelector(state => state.comment.entities);
  const commentEntity = useAppSelector(state => state.comment.entity);
  const loading = useAppSelector(state => state.comment.loading);
  const updating = useAppSelector(state => state.comment.updating);
  const updateSuccess = useAppSelector(state => state.comment.updateSuccess);
  const commentStatusValues = Object.keys(CommentStatus);

  const handleClose = () => {
    navigate(`/comment${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getLessons({}));
    dispatch(getAppUsers({}));
    dispatch(getComments({}));
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
    values.createdAt = convertDateTimeToServer(values.createdAt);
    values.updatedAt = convertDateTimeToServer(values.updatedAt);

    const entity = {
      ...commentEntity,
      ...values,
      lesson: lessons.find(it => it.id.toString() === values.lesson?.toString()),
      author: appUsers.find(it => it.id.toString() === values.author?.toString()),
      parent: comments.find(it => it.id.toString() === values.parent?.toString()),
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
          createdAt: displayDefaultDateTime(),
          updatedAt: displayDefaultDateTime(),
        }
      : {
          status: 'VISIBLE',
          ...commentEntity,
          createdAt: convertDateTimeFromServer(commentEntity.createdAt),
          updatedAt: convertDateTimeFromServer(commentEntity.updatedAt),
          lesson: commentEntity?.lesson?.id,
          author: commentEntity?.author?.id,
          parent: commentEntity?.parent?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="edupressApp.comment.home.createOrEditLabel" data-cy="CommentCreateUpdateHeading">
            <Translate contentKey="edupressApp.comment.home.createOrEditLabel">Create or edit a Comment</Translate>
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
                  id="comment-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('edupressApp.comment.content')}
                id="comment-content"
                name="content"
                data-cy="content"
                type="textarea"
              />
              <ValidatedField
                label={translate('edupressApp.comment.createdAt')}
                id="comment-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('edupressApp.comment.updatedAt')}
                id="comment-updatedAt"
                name="updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('edupressApp.comment.status')}
                id="comment-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {commentStatusValues.map(commentStatus => (
                  <option value={commentStatus} key={commentStatus}>
                    {translate(`edupressApp.CommentStatus.${commentStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                id="comment-lesson"
                name="lesson"
                data-cy="lesson"
                label={translate('edupressApp.comment.lesson')}
                type="select"
              >
                <option value="" key="0" />
                {lessons
                  ? lessons.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="comment-author"
                name="author"
                data-cy="author"
                label={translate('edupressApp.comment.author')}
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
              <ValidatedField
                id="comment-parent"
                name="parent"
                data-cy="parent"
                label={translate('edupressApp.comment.parent')}
                type="select"
              >
                <option value="" key="0" />
                {comments
                  ? comments.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/comment" replace color="info">
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

export default CommentUpdate;

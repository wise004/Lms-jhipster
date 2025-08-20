import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getCourses } from 'app/entities/course/course.reducer';
import { getEntities as getAppUsers } from 'app/entities/app-user/app-user.reducer';
import { createEntity, getEntity, reset, updateEntity } from './review.reducer';

export const ReviewUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const courses = useAppSelector(state => state.course.entities);
  const appUsers = useAppSelector(state => state.appUser.entities);
  const reviewEntity = useAppSelector(state => state.review.entity);
  const loading = useAppSelector(state => state.review.loading);
  const updating = useAppSelector(state => state.review.updating);
  const updateSuccess = useAppSelector(state => state.review.updateSuccess);

  const handleClose = () => {
    navigate(`/review${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getCourses({}));
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
    if (values.rating !== undefined && typeof values.rating !== 'number') {
      values.rating = Number(values.rating);
    }
    values.createdAt = convertDateTimeToServer(values.createdAt);

    const entity = {
      ...reviewEntity,
      ...values,
      course: courses.find(it => it.id.toString() === values.course?.toString()),
      student: appUsers.find(it => it.id.toString() === values.student?.toString()),
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
        }
      : {
          ...reviewEntity,
          createdAt: convertDateTimeFromServer(reviewEntity.createdAt),
          course: reviewEntity?.course?.id,
          student: reviewEntity?.student?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="edupressApp.review.home.createOrEditLabel" data-cy="ReviewCreateUpdateHeading">
            <Translate contentKey="edupressApp.review.home.createOrEditLabel">Create or edit a Review</Translate>
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
                  id="review-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('edupressApp.review.rating')}
                id="review-rating"
                name="rating"
                data-cy="rating"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('edupressApp.review.reviewText')}
                id="review-reviewText"
                name="reviewText"
                data-cy="reviewText"
                type="textarea"
              />
              <ValidatedField
                label={translate('edupressApp.review.createdAt')}
                id="review-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                id="review-course"
                name="course"
                data-cy="course"
                label={translate('edupressApp.review.course')}
                type="select"
              >
                <option value="" key="0" />
                {courses
                  ? courses.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="review-student"
                name="student"
                data-cy="student"
                label={translate('edupressApp.review.student')}
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/review" replace color="info">
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

export default ReviewUpdate;

import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getAppUsers } from 'app/entities/app-user/app-user.reducer';
import { getEntities as getCategories } from 'app/entities/category/category.reducer';
import { CourseStatus } from 'app/shared/model/enumerations/course-status.model';
import { createEntity, getEntity, reset, updateEntity } from './course.reducer';

export const CourseUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const appUsers = useAppSelector(state => state.appUser.entities);
  const categories = useAppSelector(state => state.category.entities);
  const courseEntity = useAppSelector(state => state.course.entity);
  const loading = useAppSelector(state => state.course.loading);
  const updating = useAppSelector(state => state.course.updating);
  const updateSuccess = useAppSelector(state => state.course.updateSuccess);
  const courseStatusValues = Object.keys(CourseStatus);

  const handleClose = () => {
    navigate(`/course${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getAppUsers({}));
    dispatch(getCategories({}));
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
    if (values.price !== undefined && typeof values.price !== 'number') {
      values.price = Number(values.price);
    }
    if (values.originalPrice !== undefined && typeof values.originalPrice !== 'number') {
      values.originalPrice = Number(values.originalPrice);
    }
    if (values.averageRating !== undefined && typeof values.averageRating !== 'number') {
      values.averageRating = Number(values.averageRating);
    }
    if (values.enrollmentCount !== undefined && typeof values.enrollmentCount !== 'number') {
      values.enrollmentCount = Number(values.enrollmentCount);
    }

    const entity = {
      ...courseEntity,
      ...values,
      instructor: appUsers.find(it => it.id.toString() === values.instructor?.toString()),
      category: categories.find(it => it.id.toString() === values.category?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          status: 'DRAFT',
          ...courseEntity,
          instructor: courseEntity?.instructor?.id,
          category: courseEntity?.category?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="edupressApp.course.home.createOrEditLabel" data-cy="CourseCreateUpdateHeading">
            <Translate contentKey="edupressApp.course.home.createOrEditLabel">Create or edit a Course</Translate>
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
                  id="course-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('edupressApp.course.title')}
                id="course-title"
                name="title"
                data-cy="title"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('edupressApp.course.slug')}
                id="course-slug"
                name="slug"
                data-cy="slug"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('edupressApp.course.shortDescription')}
                id="course-shortDescription"
                name="shortDescription"
                data-cy="shortDescription"
                type="text"
              />
              <ValidatedField
                label={translate('edupressApp.course.description')}
                id="course-description"
                name="description"
                data-cy="description"
                type="textarea"
              />
              <ValidatedField
                label={translate('edupressApp.course.thumbnailUrl')}
                id="course-thumbnailUrl"
                name="thumbnailUrl"
                data-cy="thumbnailUrl"
                type="text"
              />
              <ValidatedField
                label={translate('edupressApp.course.price')}
                id="course-price"
                name="price"
                data-cy="price"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('edupressApp.course.originalPrice')}
                id="course-originalPrice"
                name="originalPrice"
                data-cy="originalPrice"
                type="text"
              />
              <ValidatedField label={translate('edupressApp.course.level')} id="course-level" name="level" data-cy="level" type="text" />
              <ValidatedField
                label={translate('edupressApp.course.language')}
                id="course-language"
                name="language"
                data-cy="language"
                type="text"
              />
              <ValidatedField
                label={translate('edupressApp.course.status')}
                id="course-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {courseStatusValues.map(courseStatus => (
                  <option value={courseStatus} key={courseStatus}>
                    {translate(`edupressApp.CourseStatus.${courseStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('edupressApp.course.isPublished')}
                id="course-isPublished"
                name="isPublished"
                data-cy="isPublished"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('edupressApp.course.isFeatured')}
                id="course-isFeatured"
                name="isFeatured"
                data-cy="isFeatured"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('edupressApp.course.averageRating')}
                id="course-averageRating"
                name="averageRating"
                data-cy="averageRating"
                type="text"
              />
              <ValidatedField
                label={translate('edupressApp.course.enrollmentCount')}
                id="course-enrollmentCount"
                name="enrollmentCount"
                data-cy="enrollmentCount"
                type="text"
              />
              <ValidatedField
                id="course-instructor"
                name="instructor"
                data-cy="instructor"
                label={translate('edupressApp.course.instructor')}
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
                id="course-category"
                name="category"
                data-cy="category"
                label={translate('edupressApp.course.category')}
                type="select"
              >
                <option value="" key="0" />
                {categories
                  ? categories.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/course" replace color="info">
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

export default CourseUpdate;

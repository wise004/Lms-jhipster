import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import axios from 'axios';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getCourses } from 'app/entities/course/course.reducer';
import { LessonType } from 'app/shared/model/enumerations/lesson-type.model';
import { createEntity, getEntity, reset, updateEntity } from './lesson.reducer';

export const LessonUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const courses = useAppSelector(state => state.course.entities);
  const lessonEntity = useAppSelector(state => state.lesson.entity);
  const loading = useAppSelector(state => state.lesson.loading);
  const updating = useAppSelector(state => state.lesson.updating);
  const updateSuccess = useAppSelector(state => state.lesson.updateSuccess);
  const lessonTypeValues = Object.keys(LessonType);

  const handleClose = () => {
    navigate(`/lesson${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getCourses({}));
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
    if (values.duration !== undefined && typeof values.duration !== 'number') {
      values.duration = Number(values.duration);
    }
    if (values.sortOrder !== undefined && typeof values.sortOrder !== 'number') {
      values.sortOrder = Number(values.sortOrder);
    }

    const entity = {
      ...lessonEntity,
      ...values,
      course: courses.find(it => it.id.toString() === values.course?.toString()),
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
          type: 'VIDEO',
          ...lessonEntity,
          course: lessonEntity?.course?.id,
        };

  const onUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    const form = new FormData();
    form.append('file', file);
    try {
      const resp = await axios.post('/api/uploads', form, { headers: { 'Content-Type': 'multipart/form-data' } });
      const url = resp.data?.url;
      if (url) {
        // programmatically set form value
        const input = document.getElementById('lesson-videoUrl') as HTMLInputElement | null;
        if (input) input.value = url;
      }
    } catch (err) {
      // ignore simple errors for now
    }
  };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="edupressApp.lesson.home.createOrEditLabel" data-cy="LessonCreateUpdateHeading">
            <Translate contentKey="edupressApp.lesson.home.createOrEditLabel">Create or edit a Lesson</Translate>
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
                  id="lesson-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('edupressApp.lesson.title')}
                id="lesson-title"
                name="title"
                data-cy="title"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('edupressApp.lesson.description')}
                id="lesson-description"
                name="description"
                data-cy="description"
                type="textarea"
              />
              <ValidatedField
                label={translate('edupressApp.lesson.content')}
                id="lesson-content"
                name="content"
                data-cy="content"
                type="textarea"
              />
              <ValidatedField
                label={translate('edupressApp.lesson.videoUrl')}
                id="lesson-videoUrl"
                name="videoUrl"
                data-cy="videoUrl"
                type="text"
              />
              <div className="mb-3">
                <input type="file" accept="video/*" onChange={onUpload} />
              </div>
              <ValidatedField
                label={translate('edupressApp.lesson.duration')}
                id="lesson-duration"
                name="duration"
                data-cy="duration"
                type="text"
              />
              <ValidatedField label={translate('edupressApp.lesson.type')} id="lesson-type" name="type" data-cy="type" type="select">
                {lessonTypeValues.map(lessonType => (
                  <option value={lessonType} key={lessonType}>
                    {translate(`edupressApp.LessonType.${lessonType}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('edupressApp.lesson.isFree')}
                id="lesson-isFree"
                name="isFree"
                data-cy="isFree"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('edupressApp.lesson.sortOrder')}
                id="lesson-sortOrder"
                name="sortOrder"
                data-cy="sortOrder"
                type="text"
              />
              <ValidatedField
                label={translate('edupressApp.lesson.isPublished')}
                id="lesson-isPublished"
                name="isPublished"
                data-cy="isPublished"
                check
                type="checkbox"
              />
              <ValidatedField
                id="lesson-course"
                name="course"
                data-cy="course"
                label={translate('edupressApp.lesson.course')}
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/lesson" replace color="info">
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

export default LessonUpdate;

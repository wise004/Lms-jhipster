import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { Role } from 'app/shared/model/enumerations/role.model';
import { createEntity, getEntity, reset, updateEntity } from './app-user.reducer';

export const AppUserUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const appUserEntity = useAppSelector(state => state.appUser.entity);
  const loading = useAppSelector(state => state.appUser.loading);
  const updating = useAppSelector(state => state.appUser.updating);
  const updateSuccess = useAppSelector(state => state.appUser.updateSuccess);
  const roleValues = Object.keys(Role);

  const handleClose = () => {
    navigate('/app-user');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
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

    const entity = {
      ...appUserEntity,
      ...values,
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
          role: 'STUDENT',
          ...appUserEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="edupressApp.appUser.home.createOrEditLabel" data-cy="AppUserCreateUpdateHeading">
            <Translate contentKey="edupressApp.appUser.home.createOrEditLabel">Create or edit a AppUser</Translate>
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
                  id="app-user-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField label={translate('edupressApp.appUser.role')} id="app-user-role" name="role" data-cy="role" type="select">
                {roleValues.map(role => (
                  <option value={role} key={role}>
                    {translate(`edupressApp.Role.${role}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('edupressApp.appUser.firstName')}
                id="app-user-firstName"
                name="firstName"
                data-cy="firstName"
                type="text"
              />
              <ValidatedField
                label={translate('edupressApp.appUser.lastName')}
                id="app-user-lastName"
                name="lastName"
                data-cy="lastName"
                type="text"
              />
              <ValidatedField
                label={translate('edupressApp.appUser.email')}
                id="app-user-email"
                name="email"
                data-cy="email"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField label={translate('edupressApp.appUser.phone')} id="app-user-phone" name="phone" data-cy="phone" type="text" />
              <ValidatedField label={translate('edupressApp.appUser.bio')} id="app-user-bio" name="bio" data-cy="bio" type="textarea" />
              <ValidatedField
                label={translate('edupressApp.appUser.profilePictureUrl')}
                id="app-user-profilePictureUrl"
                name="profilePictureUrl"
                data-cy="profilePictureUrl"
                type="text"
              />
              <ValidatedField
                label={translate('edupressApp.appUser.isActive')}
                id="app-user-isActive"
                name="isActive"
                data-cy="isActive"
                check
                type="checkbox"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/app-user" replace color="info">
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

export default AppUserUpdate;

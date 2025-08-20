import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './app-user.reducer';

export const AppUserDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const appUserEntity = useAppSelector(state => state.appUser.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="appUserDetailsHeading">
          <Translate contentKey="edupressApp.appUser.detail.title">AppUser</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{appUserEntity.id}</dd>
          <dt>
            <span id="role">
              <Translate contentKey="edupressApp.appUser.role">Role</Translate>
            </span>
          </dt>
          <dd>{appUserEntity.role}</dd>
          <dt>
            <span id="firstName">
              <Translate contentKey="edupressApp.appUser.firstName">First Name</Translate>
            </span>
          </dt>
          <dd>{appUserEntity.firstName}</dd>
          <dt>
            <span id="lastName">
              <Translate contentKey="edupressApp.appUser.lastName">Last Name</Translate>
            </span>
          </dt>
          <dd>{appUserEntity.lastName}</dd>
          <dt>
            <span id="email">
              <Translate contentKey="edupressApp.appUser.email">Email</Translate>
            </span>
          </dt>
          <dd>{appUserEntity.email}</dd>
          <dt>
            <span id="phone">
              <Translate contentKey="edupressApp.appUser.phone">Phone</Translate>
            </span>
          </dt>
          <dd>{appUserEntity.phone}</dd>
          <dt>
            <span id="bio">
              <Translate contentKey="edupressApp.appUser.bio">Bio</Translate>
            </span>
          </dt>
          <dd>{appUserEntity.bio}</dd>
          <dt>
            <span id="profilePictureUrl">
              <Translate contentKey="edupressApp.appUser.profilePictureUrl">Profile Picture Url</Translate>
            </span>
          </dt>
          <dd>{appUserEntity.profilePictureUrl}</dd>
          <dt>
            <span id="isActive">
              <Translate contentKey="edupressApp.appUser.isActive">Is Active</Translate>
            </span>
          </dt>
          <dd>{appUserEntity.isActive ? 'true' : 'false'}</dd>
        </dl>
        <Button tag={Link} to="/app-user" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/app-user/${appUserEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default AppUserDetail;

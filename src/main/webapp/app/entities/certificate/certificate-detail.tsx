import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './certificate.reducer';

export const CertificateDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const certificateEntity = useAppSelector(state => state.certificate.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="certificateDetailsHeading">
          <Translate contentKey="edupressApp.certificate.detail.title">Certificate</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{certificateEntity.id}</dd>
          <dt>
            <span id="url">
              <Translate contentKey="edupressApp.certificate.url">Url</Translate>
            </span>
          </dt>
          <dd>{certificateEntity.url}</dd>
          <dt>
            <span id="issuedAt">
              <Translate contentKey="edupressApp.certificate.issuedAt">Issued At</Translate>
            </span>
          </dt>
          <dd>
            {certificateEntity.issuedAt ? <TextFormat value={certificateEntity.issuedAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="status">
              <Translate contentKey="edupressApp.certificate.status">Status</Translate>
            </span>
          </dt>
          <dd>{certificateEntity.status}</dd>
          <dt>
            <Translate contentKey="edupressApp.certificate.enrollment">Enrollment</Translate>
          </dt>
          <dd>{certificateEntity.enrollment ? certificateEntity.enrollment.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/certificate" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/certificate/${certificateEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CertificateDetail;

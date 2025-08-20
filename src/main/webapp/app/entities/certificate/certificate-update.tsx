import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getEnrollments } from 'app/entities/enrollment/enrollment.reducer';
import { CertificateStatus } from 'app/shared/model/enumerations/certificate-status.model';
import { createEntity, getEntity, reset, updateEntity } from './certificate.reducer';

export const CertificateUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const enrollments = useAppSelector(state => state.enrollment.entities);
  const certificateEntity = useAppSelector(state => state.certificate.entity);
  const loading = useAppSelector(state => state.certificate.loading);
  const updating = useAppSelector(state => state.certificate.updating);
  const updateSuccess = useAppSelector(state => state.certificate.updateSuccess);
  const certificateStatusValues = Object.keys(CertificateStatus);

  const handleClose = () => {
    navigate(`/certificate${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getEnrollments({}));
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
    values.issuedAt = convertDateTimeToServer(values.issuedAt);

    const entity = {
      ...certificateEntity,
      ...values,
      enrollment: enrollments.find(it => it.id.toString() === values.enrollment?.toString()),
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
          issuedAt: displayDefaultDateTime(),
        }
      : {
          status: 'GENERATED',
          ...certificateEntity,
          issuedAt: convertDateTimeFromServer(certificateEntity.issuedAt),
          enrollment: certificateEntity?.enrollment?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="edupressApp.certificate.home.createOrEditLabel" data-cy="CertificateCreateUpdateHeading">
            <Translate contentKey="edupressApp.certificate.home.createOrEditLabel">Create or edit a Certificate</Translate>
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
                  id="certificate-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('edupressApp.certificate.url')}
                id="certificate-url"
                name="url"
                data-cy="url"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('edupressApp.certificate.issuedAt')}
                id="certificate-issuedAt"
                name="issuedAt"
                data-cy="issuedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('edupressApp.certificate.status')}
                id="certificate-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {certificateStatusValues.map(certificateStatus => (
                  <option value={certificateStatus} key={certificateStatus}>
                    {translate(`edupressApp.CertificateStatus.${certificateStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                id="certificate-enrollment"
                name="enrollment"
                data-cy="enrollment"
                label={translate('edupressApp.certificate.enrollment')}
                type="select"
              >
                <option value="" key="0" />
                {enrollments
                  ? enrollments.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/certificate" replace color="info">
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

export default CertificateUpdate;

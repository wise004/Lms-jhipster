import React from 'react';
import { Link } from 'react-router-dom';
import { Row, Col, Alert, Button, Card, CardBody } from 'reactstrap';
import { ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { handleRegister, reset } from './register.reducer';

import './register.scss';

export const RegisterPage = () => {
  const dispatch = useAppDispatch();

  const handleValidSubmit = ({ email, firstPassword }) => {
    dispatch(handleRegister({ login: email, email, password: firstPassword, langKey: 'en' }));
  };

  const registerState = useAppSelector(state => state.register);

  React.useEffect(() => {
    dispatch(reset());
  }, [dispatch]);

  return (
    <div className="register-page">
      <div className="register-hero">
        <div className="container">
          <Row className="justify-content-center">
            <Col md="8" lg="6">
              <Card className="register-card">
                <CardBody>
                  <div className="text-center mb-4">
                    <h1 className="register-title">Create Your Account</h1>
                    <p className="register-subtitle">Join thousands of learners and start your educational journey today</p>
                  </div>

                  <Row>
                    <Col md="12">
                      {registerState.successMessage ? (
                        <Alert color="success" className="success-alert">
                          <i className="fas fa-check-circle me-2"></i>
                          {registerState.successMessage}
                        </Alert>
                      ) : null}

                      {registerState.errorMessage ? (
                        <Alert color="danger" className="error-alert">
                          <i className="fas fa-exclamation-triangle me-2"></i>
                          {registerState.errorMessage}
                        </Alert>
                      ) : null}
                    </Col>
                    <Col md="12">
                      <ValidatedForm onSubmit={handleValidSubmit}>
                        <ValidatedField
                          name="email"
                          label={translate('global.form.email.label')}
                          placeholder={translate('global.form.email.placeholder')}
                          type="email"
                          validate={{
                            required: { value: true, message: translate('global.messages.validate.email.required') },
                            minLength: { value: 5, message: translate('global.messages.validate.email.minlength') },
                            maxLength: { value: 254, message: translate('global.messages.validate.email.maxlength') },
                          }}
                          data-cy="email"
                        />
                        <ValidatedField
                          name="firstPassword"
                          label={translate('global.form.newpassword.label')}
                          placeholder={translate('global.form.newpassword.placeholder')}
                          type="password"
                          validate={{
                            required: { value: true, message: translate('global.messages.validate.newpassword.required') },
                            minLength: { value: 4, message: translate('global.messages.validate.newpassword.minlength') },
                            maxLength: { value: 50, message: translate('global.messages.validate.newpassword.maxlength') },
                          }}
                          data-cy="firstPassword"
                        />
                        <ValidatedField
                          name="secondPassword"
                          label={translate('global.form.confirmpassword.label')}
                          placeholder={translate('global.form.confirmpassword.placeholder')}
                          type="password"
                          validate={{
                            required: { value: true, message: translate('global.messages.validate.confirmpassword.required') },
                            minLength: { value: 4, message: translate('global.messages.validate.confirmpassword.minlength') },
                            maxLength: { value: 50, message: translate('global.messages.validate.confirmpassword.maxlength') },
                          }}
                          data-cy="secondPassword"
                        />
                        <Button id="register-submit" color="primary" type="submit" size="lg" className="register-btn" data-cy="submit">
                          <i className="fas fa-user-plus me-2"></i>
                          {translate('register.form.button')}
                        </Button>
                      </ValidatedForm>

                      <div className="text-center mt-4">
                        <p className="login-link">
                          Already have an account?{' '}
                          <Link to="/login" className="signin-link">
                            Sign in here
                          </Link>
                        </p>
                      </div>
                    </Col>
                  </Row>
                </CardBody>
              </Card>
            </Col>
          </Row>
        </div>
      </div>
    </div>
  );
};

export default RegisterPage;

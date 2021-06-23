import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IAction } from 'app/shared/model/action.model';
import { getEntities as getActions } from 'app/entities/action/action.reducer';
import { IUser } from 'app/shared/model/user.model';
import { getUsers } from 'app/shared/reducers/user-management';
import { getEntity, updateEntity, createEntity, reset } from './action-change-mail.reducer';
import { IActionChangeMail } from 'app/shared/model/action-change-mail.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IActionChangeMailUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const ActionChangeMailUpdate = (props: IActionChangeMailUpdateProps) => {
  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const { actionChangeMailEntity, actions, users, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/action-change-mail');
  };

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntity(props.match.params.id);
    }

    props.getActions();
    props.getUsers();
  }, []);

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const entity = {
        ...actionChangeMailEntity,
        ...values,
        action: actions.find(it => it.id.toString() === values.actionId.toString()),
        user: users.find(it => it.id.toString() === values.userId.toString()),
      };

      if (isNew) {
        props.createEntity(entity);
      } else {
        props.updateEntity(entity);
      }
    }
  };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="actionfpApp.actionChangeMail.home.createOrEditLabel" data-cy="ActionChangeMailCreateUpdateHeading">
            Create or edit a ActionChangeMail
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : actionChangeMailEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="action-change-mail-id">Id</Label>
                  <AvInput id="action-change-mail-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="actionTypeLabel" for="action-change-mail-actionType">
                  Action Type
                </Label>
                <AvField id="action-change-mail-actionType" data-cy="actionType" type="text" name="actionType" />
              </AvGroup>
              <AvGroup>
                <Label for="action-change-mail-action">Action</Label>
                <AvInput id="action-change-mail-action" data-cy="action" type="select" className="form-control" name="actionId">
                  <option value="" key="0" />
                  {actions
                    ? actions.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.id}
                        </option>
                      ))
                    : null}
                </AvInput>
              </AvGroup>
              <AvGroup>
                <Label for="action-change-mail-user">User</Label>
                <AvInput id="action-change-mail-user" data-cy="user" type="select" className="form-control" name="userId">
                  <option value="" key="0" />
                  {users
                    ? users.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.id}
                        </option>
                      ))
                    : null}
                </AvInput>
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/action-change-mail" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">Back</span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp; Save
              </Button>
            </AvForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

const mapStateToProps = (storeState: IRootState) => ({
  actions: storeState.action.entities,
  users: storeState.userManagement.users,
  actionChangeMailEntity: storeState.actionChangeMail.entity,
  loading: storeState.actionChangeMail.loading,
  updating: storeState.actionChangeMail.updating,
  updateSuccess: storeState.actionChangeMail.updateSuccess,
});

const mapDispatchToProps = {
  getActions,
  getUsers,
  getEntity,
  updateEntity,
  createEntity,
  reset,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(ActionChangeMailUpdate);

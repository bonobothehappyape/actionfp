import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IUnit } from 'app/shared/model/unit.model';
import { getEntities as getUnits } from 'app/entities/unit/unit.reducer';
import { IAction } from 'app/shared/model/action.model';
import { getEntities as getActions } from 'app/entities/action/action.reducer';
import { getEntity, updateEntity, createEntity, reset } from './framework.reducer';
import { IFramework } from 'app/shared/model/framework.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IFrameworkUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const FrameworkUpdate = (props: IFrameworkUpdateProps) => {
  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const { frameworkEntity, units, actions, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/framework');
  };

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntity(props.match.params.id);
    }

    props.getUnits();
    props.getActions();
  }, []);

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const entity = {
        ...frameworkEntity,
        ...values,
        unit: units.find(it => it.id.toString() === values.unitId.toString()),
        action: actions.find(it => it.id.toString() === values.actionId.toString()),
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
          <h2 id="actionfpApp.framework.home.createOrEditLabel" data-cy="FrameworkCreateUpdateHeading">
            Create or edit a Framework
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : frameworkEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="framework-id">Id</Label>
                  <AvInput id="framework-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="yearLabel" for="framework-year">
                  Year
                </Label>
                <AvField id="framework-year" data-cy="year" type="string" className="form-control" name="year" />
              </AvGroup>
              <AvGroup>
                <Label id="nameLabel" for="framework-name">
                  Name
                </Label>
                <AvField id="framework-name" data-cy="name" type="text" name="name" />
              </AvGroup>
              <AvGroup>
                <Label id="typeLabel" for="framework-type">
                  Type
                </Label>
                <AvField id="framework-type" data-cy="type" type="string" className="form-control" name="type" />
              </AvGroup>
              <AvGroup>
                <Label id="descriptionLabel" for="framework-description">
                  Description
                </Label>
                <AvField id="framework-description" data-cy="description" type="text" name="description" />
              </AvGroup>
              <AvGroup>
                <Label for="framework-unit">Unit</Label>
                <AvInput id="framework-unit" data-cy="unit" type="select" className="form-control" name="unitId">
                  <option value="" key="0" />
                  {units
                    ? units.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.id}
                        </option>
                      ))
                    : null}
                </AvInput>
              </AvGroup>
              <AvGroup>
                <Label for="framework-action">Action</Label>
                <AvInput id="framework-action" data-cy="action" type="select" className="form-control" name="actionId">
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
              <Button tag={Link} id="cancel-save" to="/framework" replace color="info">
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
  units: storeState.unit.entities,
  actions: storeState.action.entities,
  frameworkEntity: storeState.framework.entity,
  loading: storeState.framework.loading,
  updating: storeState.framework.updating,
  updateSuccess: storeState.framework.updateSuccess,
});

const mapDispatchToProps = {
  getUnits,
  getActions,
  getEntity,
  updateEntity,
  createEntity,
  reset,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(FrameworkUpdate);

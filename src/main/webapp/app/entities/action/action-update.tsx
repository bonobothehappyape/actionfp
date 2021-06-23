import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IICSRecomm } from 'app/shared/model/ics-recomm.model';
import { getEntities as getICsRecomms } from 'app/entities/ics-recomm/ics-recomm.reducer';
import { IUnit } from 'app/shared/model/unit.model';
import { getEntities as getUnits } from 'app/entities/unit/unit.reducer';
import { IStatus } from 'app/shared/model/status.model';
import { getEntities as getStatuses } from 'app/entities/status/status.reducer';
import { getEntity, updateEntity, createEntity, reset } from './action.reducer';
import { IAction } from 'app/shared/model/action.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IActionUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const ActionUpdate = (props: IActionUpdateProps) => {
  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const { actionEntity, iCSRecomms, units, statuses, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/action');
  };

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntity(props.match.params.id);
    }

    props.getICsRecomms();
    props.getUnits();
    props.getStatuses();
  }, []);

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const entity = {
        ...actionEntity,
        ...values,
        icsRecomm: iCSRecomms.find(it => it.id.toString() === values.icsRecommId.toString()),
        ownerUnit: units.find(it => it.id.toString() === values.ownerUnitId.toString()),
        status: statuses.find(it => it.id.toString() === values.statusId.toString()),
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
          <h2 id="actionfpApp.action.home.createOrEditLabel" data-cy="ActionCreateUpdateHeading">
            Create or edit a Action
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : actionEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="action-id">Id</Label>
                  <AvInput id="action-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="taskNameLabel" for="action-taskName">
                  Task Name
                </Label>
                <AvField id="action-taskName" data-cy="taskName" type="text" name="taskName" />
              </AvGroup>
              <AvGroup>
                <Label id="taskDescriptionLabel" for="action-taskDescription">
                  Task Description
                </Label>
                <AvField id="action-taskDescription" data-cy="taskDescription" type="text" name="taskDescription" />
              </AvGroup>
              <AvGroup check>
                <Label id="requiresPeriodicFollowupLabel">
                  <AvInput
                    id="action-requiresPeriodicFollowup"
                    data-cy="requiresPeriodicFollowup"
                    type="checkbox"
                    className="form-check-input"
                    name="requiresPeriodicFollowup"
                  />
                  Requires Periodic Followup
                </Label>
              </AvGroup>
              <AvGroup>
                <Label id="initialDeadlineLabel" for="action-initialDeadline">
                  Initial Deadline
                </Label>
                <AvField
                  id="action-initialDeadline"
                  data-cy="initialDeadline"
                  type="date"
                  className="form-control"
                  name="initialDeadline"
                />
              </AvGroup>
              <AvGroup>
                <Label id="updatedDeadlineLabel" for="action-updatedDeadline">
                  Updated Deadline
                </Label>
                <AvField
                  id="action-updatedDeadline"
                  data-cy="updatedDeadline"
                  type="date"
                  className="form-control"
                  name="updatedDeadline"
                />
              </AvGroup>
              <AvGroup>
                <Label id="doneDateLabel" for="action-doneDate">
                  Done Date
                </Label>
                <AvField id="action-doneDate" data-cy="doneDate" type="date" className="form-control" name="doneDate" />
              </AvGroup>
              <AvGroup>
                <Label id="verifiedDateLabel" for="action-verifiedDate">
                  Verified Date
                </Label>
                <AvField id="action-verifiedDate" data-cy="verifiedDate" type="date" className="form-control" name="verifiedDate" />
              </AvGroup>
              <AvGroup>
                <Label for="action-icsRecomm">Ics Recomm</Label>
                <AvInput id="action-icsRecomm" data-cy="icsRecomm" type="select" className="form-control" name="icsRecommId">
                  <option value="" key="0" />
                  {iCSRecomms
                    ? iCSRecomms.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.id}
                        </option>
                      ))
                    : null}
                </AvInput>
              </AvGroup>
              <AvGroup>
                <Label for="action-ownerUnit">Owner Unit</Label>
                <AvInput id="action-ownerUnit" data-cy="ownerUnit" type="select" className="form-control" name="ownerUnitId">
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
                <Label for="action-status">Status</Label>
                <AvInput id="action-status" data-cy="status" type="select" className="form-control" name="statusId">
                  <option value="" key="0" />
                  {statuses
                    ? statuses.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.id}
                        </option>
                      ))
                    : null}
                </AvInput>
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/action" replace color="info">
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
  iCSRecomms: storeState.iCSRecomm.entities,
  units: storeState.unit.entities,
  statuses: storeState.status.entities,
  actionEntity: storeState.action.entity,
  loading: storeState.action.loading,
  updating: storeState.action.updating,
  updateSuccess: storeState.action.updateSuccess,
});

const mapDispatchToProps = {
  getICsRecomms,
  getUnits,
  getStatuses,
  getEntity,
  updateEntity,
  createEntity,
  reset,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(ActionUpdate);

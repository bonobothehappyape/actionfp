import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IStatus } from 'app/shared/model/status.model';
import { getEntities as getStatuses } from 'app/entities/status/status.reducer';
import { IAuditRecomm } from 'app/shared/model/audit-recomm.model';
import { getEntities as getAuditRecomms } from 'app/entities/audit-recomm/audit-recomm.reducer';
import { getEntity, updateEntity, createEntity, reset } from './audit-sub-recomm.reducer';
import { IAuditSubRecomm } from 'app/shared/model/audit-sub-recomm.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IAuditSubRecommUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const AuditSubRecommUpdate = (props: IAuditSubRecommUpdateProps) => {
  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const { auditSubRecommEntity, statuses, auditRecomms, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/audit-sub-recomm');
  };

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntity(props.match.params.id);
    }

    props.getStatuses();
    props.getAuditRecomms();
  }, []);

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const entity = {
        ...auditSubRecommEntity,
        ...values,
        status: statuses.find(it => it.id.toString() === values.statusId.toString()),
        auditRecomm: auditRecomms.find(it => it.id.toString() === values.auditRecommId.toString()),
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
          <h2 id="actionfpApp.auditSubRecomm.home.createOrEditLabel" data-cy="AuditSubRecommCreateUpdateHeading">
            Create or edit a AuditSubRecomm
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : auditSubRecommEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="audit-sub-recomm-id">Id</Label>
                  <AvInput id="audit-sub-recomm-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="subRecommNumLabel" for="audit-sub-recomm-subRecommNum">
                  Sub Recomm Num
                </Label>
                <AvField id="audit-sub-recomm-subRecommNum" data-cy="subRecommNum" type="text" name="subRecommNum" />
              </AvGroup>
              <AvGroup>
                <Label id="descriptionLabel" for="audit-sub-recomm-description">
                  Description
                </Label>
                <AvField id="audit-sub-recomm-description" data-cy="description" type="text" name="description" />
              </AvGroup>
              <AvGroup>
                <Label for="audit-sub-recomm-status">Status</Label>
                <AvInput id="audit-sub-recomm-status" data-cy="status" type="select" className="form-control" name="statusId">
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
              <AvGroup>
                <Label for="audit-sub-recomm-auditRecomm">Audit Recomm</Label>
                <AvInput
                  id="audit-sub-recomm-auditRecomm"
                  data-cy="auditRecomm"
                  type="select"
                  className="form-control"
                  name="auditRecommId"
                >
                  <option value="" key="0" />
                  {auditRecomms
                    ? auditRecomms.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.id}
                        </option>
                      ))
                    : null}
                </AvInput>
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/audit-sub-recomm" replace color="info">
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
  statuses: storeState.status.entities,
  auditRecomms: storeState.auditRecomm.entities,
  auditSubRecommEntity: storeState.auditSubRecomm.entity,
  loading: storeState.auditSubRecomm.loading,
  updating: storeState.auditSubRecomm.updating,
  updateSuccess: storeState.auditSubRecomm.updateSuccess,
});

const mapDispatchToProps = {
  getStatuses,
  getAuditRecomms,
  getEntity,
  updateEntity,
  createEntity,
  reset,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(AuditSubRecommUpdate);

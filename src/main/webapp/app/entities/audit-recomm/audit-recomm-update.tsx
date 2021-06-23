import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IAuditReport } from 'app/shared/model/audit-report.model';
import { getEntities as getAuditReports } from 'app/entities/audit-report/audit-report.reducer';
import { IStatus } from 'app/shared/model/status.model';
import { getEntities as getStatuses } from 'app/entities/status/status.reducer';
import { getEntity, updateEntity, createEntity, reset } from './audit-recomm.reducer';
import { IAuditRecomm } from 'app/shared/model/audit-recomm.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IAuditRecommUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const AuditRecommUpdate = (props: IAuditRecommUpdateProps) => {
  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const { auditRecommEntity, auditReports, statuses, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/audit-recomm');
  };

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntity(props.match.params.id);
    }

    props.getAuditReports();
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
        ...auditRecommEntity,
        ...values,
        report: auditReports.find(it => it.id.toString() === values.reportId.toString()),
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
          <h2 id="actionfpApp.auditRecomm.home.createOrEditLabel" data-cy="AuditRecommCreateUpdateHeading">
            Create or edit a AuditRecomm
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : auditRecommEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="audit-recomm-id">Id</Label>
                  <AvInput id="audit-recomm-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="recommNumberLabel" for="audit-recomm-recommNumber">
                  Recomm Number
                </Label>
                <AvField id="audit-recomm-recommNumber" data-cy="recommNumber" type="text" name="recommNumber" />
              </AvGroup>
              <AvGroup>
                <Label id="priorityLabel" for="audit-recomm-priority">
                  Priority
                </Label>
                <AvField id="audit-recomm-priority" data-cy="priority" type="string" className="form-control" name="priority" />
              </AvGroup>
              <AvGroup>
                <Label id="descriptionLabel" for="audit-recomm-description">
                  Description
                </Label>
                <AvField id="audit-recomm-description" data-cy="description" type="text" name="description" />
              </AvGroup>
              <AvGroup>
                <Label for="audit-recomm-report">Report</Label>
                <AvInput id="audit-recomm-report" data-cy="report" type="select" className="form-control" name="reportId">
                  <option value="" key="0" />
                  {auditReports
                    ? auditReports.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.id}
                        </option>
                      ))
                    : null}
                </AvInput>
              </AvGroup>
              <AvGroup>
                <Label for="audit-recomm-status">Status</Label>
                <AvInput id="audit-recomm-status" data-cy="status" type="select" className="form-control" name="statusId">
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
              <Button tag={Link} id="cancel-save" to="/audit-recomm" replace color="info">
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
  auditReports: storeState.auditReport.entities,
  statuses: storeState.status.entities,
  auditRecommEntity: storeState.auditRecomm.entity,
  loading: storeState.auditRecomm.loading,
  updating: storeState.auditRecomm.updating,
  updateSuccess: storeState.auditRecomm.updateSuccess,
});

const mapDispatchToProps = {
  getAuditReports,
  getStatuses,
  getEntity,
  updateEntity,
  createEntity,
  reset,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(AuditRecommUpdate);

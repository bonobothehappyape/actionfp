import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { getEntity, updateEntity, createEntity, reset } from './audit-report.reducer';
import { IAuditReport } from 'app/shared/model/audit-report.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IAuditReportUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const AuditReportUpdate = (props: IAuditReportUpdateProps) => {
  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const { auditReportEntity, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/audit-report');
  };

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntity(props.match.params.id);
    }
  }, []);

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const entity = {
        ...auditReportEntity,
        ...values,
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
          <h2 id="actionfpApp.auditReport.home.createOrEditLabel" data-cy="AuditReportCreateUpdateHeading">
            Create or edit a AuditReport
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : auditReportEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="audit-report-id">Id</Label>
                  <AvInput id="audit-report-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="yearLabel" for="audit-report-year">
                  Year
                </Label>
                <AvField id="audit-report-year" data-cy="year" type="string" className="form-control" name="year" />
              </AvGroup>
              <AvGroup>
                <Label id="reportTitleLabel" for="audit-report-reportTitle">
                  Report Title
                </Label>
                <AvField id="audit-report-reportTitle" data-cy="reportTitle" type="text" name="reportTitle" />
              </AvGroup>
              <AvGroup>
                <Label id="institutionLabel" for="audit-report-institution">
                  Institution
                </Label>
                <AvField id="audit-report-institution" data-cy="institution" type="string" className="form-control" name="institution" />
              </AvGroup>
              <AvGroup>
                <Label id="reportDescriptionLabel" for="audit-report-reportDescription">
                  Report Description
                </Label>
                <AvField id="audit-report-reportDescription" data-cy="reportDescription" type="text" name="reportDescription" />
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/audit-report" replace color="info">
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
  auditReportEntity: storeState.auditReport.entity,
  loading: storeState.auditReport.loading,
  updating: storeState.auditReport.updating,
  updateSuccess: storeState.auditReport.updateSuccess,
});

const mapDispatchToProps = {
  getEntity,
  updateEntity,
  createEntity,
  reset,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(AuditReportUpdate);

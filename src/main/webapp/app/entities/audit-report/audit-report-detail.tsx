import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './audit-report.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IAuditReportDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const AuditReportDetail = (props: IAuditReportDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { auditReportEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="auditReportDetailsHeading">AuditReport</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">Id</span>
          </dt>
          <dd>{auditReportEntity.id}</dd>
          <dt>
            <span id="year">Year</span>
          </dt>
          <dd>{auditReportEntity.year}</dd>
          <dt>
            <span id="reportTitle">Report Title</span>
          </dt>
          <dd>{auditReportEntity.reportTitle}</dd>
          <dt>
            <span id="institution">Institution</span>
          </dt>
          <dd>{auditReportEntity.institution}</dd>
          <dt>
            <span id="reportDescription">Report Description</span>
          </dt>
          <dd>{auditReportEntity.reportDescription}</dd>
        </dl>
        <Button tag={Link} to="/audit-report" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/audit-report/${auditReportEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ auditReport }: IRootState) => ({
  auditReportEntity: auditReport.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(AuditReportDetail);

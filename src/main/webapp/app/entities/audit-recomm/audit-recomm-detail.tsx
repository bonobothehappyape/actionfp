import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './audit-recomm.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IAuditRecommDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const AuditRecommDetail = (props: IAuditRecommDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { auditRecommEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="auditRecommDetailsHeading">AuditRecomm</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">Id</span>
          </dt>
          <dd>{auditRecommEntity.id}</dd>
          <dt>
            <span id="recommNumber">Recomm Number</span>
          </dt>
          <dd>{auditRecommEntity.recommNumber}</dd>
          <dt>
            <span id="priority">Priority</span>
          </dt>
          <dd>{auditRecommEntity.priority}</dd>
          <dt>
            <span id="description">Description</span>
          </dt>
          <dd>{auditRecommEntity.description}</dd>
          <dt>Report</dt>
          <dd>{auditRecommEntity.report ? auditRecommEntity.report.id : ''}</dd>
          <dt>Status</dt>
          <dd>{auditRecommEntity.status ? auditRecommEntity.status.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/audit-recomm" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/audit-recomm/${auditRecommEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ auditRecomm }: IRootState) => ({
  auditRecommEntity: auditRecomm.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(AuditRecommDetail);

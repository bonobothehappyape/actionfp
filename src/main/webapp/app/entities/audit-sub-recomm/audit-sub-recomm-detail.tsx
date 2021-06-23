import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './audit-sub-recomm.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IAuditSubRecommDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const AuditSubRecommDetail = (props: IAuditSubRecommDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { auditSubRecommEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="auditSubRecommDetailsHeading">AuditSubRecomm</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">Id</span>
          </dt>
          <dd>{auditSubRecommEntity.id}</dd>
          <dt>
            <span id="subRecommNum">Sub Recomm Num</span>
          </dt>
          <dd>{auditSubRecommEntity.subRecommNum}</dd>
          <dt>
            <span id="description">Description</span>
          </dt>
          <dd>{auditSubRecommEntity.description}</dd>
          <dt>Status</dt>
          <dd>{auditSubRecommEntity.status ? auditSubRecommEntity.status.id : ''}</dd>
          <dt>Audit Recomm</dt>
          <dd>{auditSubRecommEntity.auditRecomm ? auditSubRecommEntity.auditRecomm.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/audit-sub-recomm" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/audit-sub-recomm/${auditSubRecommEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ auditSubRecomm }: IRootState) => ({
  auditSubRecommEntity: auditSubRecomm.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(AuditSubRecommDetail);

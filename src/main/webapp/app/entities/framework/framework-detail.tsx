import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './framework.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IFrameworkDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const FrameworkDetail = (props: IFrameworkDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { frameworkEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="frameworkDetailsHeading">Framework</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">Id</span>
          </dt>
          <dd>{frameworkEntity.id}</dd>
          <dt>
            <span id="year">Year</span>
          </dt>
          <dd>{frameworkEntity.year}</dd>
          <dt>
            <span id="name">Name</span>
          </dt>
          <dd>{frameworkEntity.name}</dd>
          <dt>
            <span id="type">Type</span>
          </dt>
          <dd>{frameworkEntity.type}</dd>
          <dt>
            <span id="description">Description</span>
          </dt>
          <dd>{frameworkEntity.description}</dd>
          <dt>Unit</dt>
          <dd>{frameworkEntity.unit ? frameworkEntity.unit.id : ''}</dd>
          <dt>Action</dt>
          <dd>{frameworkEntity.action ? frameworkEntity.action.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/framework" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/framework/${frameworkEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ framework }: IRootState) => ({
  frameworkEntity: framework.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(FrameworkDetail);

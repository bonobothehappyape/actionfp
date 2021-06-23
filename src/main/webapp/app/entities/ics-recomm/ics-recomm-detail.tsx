import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './ics-recomm.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IICSRecommDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const ICSRecommDetail = (props: IICSRecommDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { iCSRecommEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="iCSRecommDetailsHeading">ICSRecomm</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">Id</span>
          </dt>
          <dd>{iCSRecommEntity.id}</dd>
          <dt>
            <span id="year">Year</span>
          </dt>
          <dd>{iCSRecommEntity.year}</dd>
          <dt>
            <span id="icsNumber">Ics Number</span>
          </dt>
          <dd>{iCSRecommEntity.icsNumber}</dd>
          <dt>
            <span id="icsDescr">Ics Descr</span>
          </dt>
          <dd>{iCSRecommEntity.icsDescr}</dd>
          <dt>
            <span id="title">Title</span>
          </dt>
          <dd>{iCSRecommEntity.title}</dd>
        </dl>
        <Button tag={Link} to="/ics-recomm" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/ics-recomm/${iCSRecommEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ iCSRecomm }: IRootState) => ({
  iCSRecommEntity: iCSRecomm.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(ICSRecommDetail);

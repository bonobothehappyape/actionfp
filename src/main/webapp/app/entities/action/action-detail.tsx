import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './action.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IActionDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const ActionDetail = (props: IActionDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { actionEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="actionDetailsHeading">Action</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">Id</span>
          </dt>
          <dd>{actionEntity.id}</dd>
          <dt>
            <span id="taskName">Task Name</span>
          </dt>
          <dd>{actionEntity.taskName}</dd>
          <dt>
            <span id="taskDescription">Task Description</span>
          </dt>
          <dd>{actionEntity.taskDescription}</dd>
          <dt>
            <span id="requiresPeriodicFollowup">Requires Periodic Followup</span>
          </dt>
          <dd>{actionEntity.requiresPeriodicFollowup ? 'true' : 'false'}</dd>
          <dt>
            <span id="initialDeadline">Initial Deadline</span>
          </dt>
          <dd>
            {actionEntity.initialDeadline ? (
              <TextFormat value={actionEntity.initialDeadline} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="updatedDeadline">Updated Deadline</span>
          </dt>
          <dd>
            {actionEntity.updatedDeadline ? (
              <TextFormat value={actionEntity.updatedDeadline} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="doneDate">Done Date</span>
          </dt>
          <dd>{actionEntity.doneDate ? <TextFormat value={actionEntity.doneDate} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="verifiedDate">Verified Date</span>
          </dt>
          <dd>
            {actionEntity.verifiedDate ? <TextFormat value={actionEntity.verifiedDate} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}
          </dd>
          <dt>Ics Recomm</dt>
          <dd>{actionEntity.icsRecomm ? actionEntity.icsRecomm.id : ''}</dd>
          <dt>Owner Unit</dt>
          <dd>{actionEntity.ownerUnit ? actionEntity.ownerUnit.id : ''}</dd>
          <dt>Status</dt>
          <dd>{actionEntity.status ? actionEntity.status.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/action" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/action/${actionEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ action }: IRootState) => ({
  actionEntity: action.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(ActionDetail);

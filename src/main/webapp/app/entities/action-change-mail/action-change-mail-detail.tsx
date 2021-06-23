import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './action-change-mail.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IActionChangeMailDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const ActionChangeMailDetail = (props: IActionChangeMailDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { actionChangeMailEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="actionChangeMailDetailsHeading">ActionChangeMail</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">Id</span>
          </dt>
          <dd>{actionChangeMailEntity.id}</dd>
          <dt>
            <span id="actionType">Action Type</span>
          </dt>
          <dd>{actionChangeMailEntity.actionType}</dd>
          <dt>Action</dt>
          <dd>{actionChangeMailEntity.action ? actionChangeMailEntity.action.id : ''}</dd>
          <dt>User</dt>
          <dd>{actionChangeMailEntity.user ? actionChangeMailEntity.user.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/action-change-mail" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/action-change-mail/${actionChangeMailEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ actionChangeMail }: IRootState) => ({
  actionChangeMailEntity: actionChangeMail.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(ActionChangeMailDetail);

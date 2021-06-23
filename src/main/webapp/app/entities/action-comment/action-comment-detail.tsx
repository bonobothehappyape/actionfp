import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './action-comment.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IActionCommentDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const ActionCommentDetail = (props: IActionCommentDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { actionCommentEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="actionCommentDetailsHeading">ActionComment</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">Id</span>
          </dt>
          <dd>{actionCommentEntity.id}</dd>
          <dt>
            <span id="comment">Comment</span>
          </dt>
          <dd>{actionCommentEntity.comment}</dd>
          <dt>Action</dt>
          <dd>{actionCommentEntity.action ? actionCommentEntity.action.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/action-comment" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/action-comment/${actionCommentEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ actionComment }: IRootState) => ({
  actionCommentEntity: actionComment.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(ActionCommentDetail);

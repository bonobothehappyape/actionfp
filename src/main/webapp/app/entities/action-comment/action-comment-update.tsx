import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IAction } from 'app/shared/model/action.model';
import { getEntities as getActions } from 'app/entities/action/action.reducer';
import { getEntity, updateEntity, createEntity, reset } from './action-comment.reducer';
import { IActionComment } from 'app/shared/model/action-comment.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IActionCommentUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const ActionCommentUpdate = (props: IActionCommentUpdateProps) => {
  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const { actionCommentEntity, actions, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/action-comment');
  };

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntity(props.match.params.id);
    }

    props.getActions();
  }, []);

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const entity = {
        ...actionCommentEntity,
        ...values,
        action: actions.find(it => it.id.toString() === values.actionId.toString()),
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
          <h2 id="actionfpApp.actionComment.home.createOrEditLabel" data-cy="ActionCommentCreateUpdateHeading">
            Create or edit a ActionComment
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : actionCommentEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="action-comment-id">Id</Label>
                  <AvInput id="action-comment-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="commentLabel" for="action-comment-comment">
                  Comment
                </Label>
                <AvField id="action-comment-comment" data-cy="comment" type="text" name="comment" />
              </AvGroup>
              <AvGroup>
                <Label for="action-comment-action">Action</Label>
                <AvInput id="action-comment-action" data-cy="action" type="select" className="form-control" name="actionId">
                  <option value="" key="0" />
                  {actions
                    ? actions.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.id}
                        </option>
                      ))
                    : null}
                </AvInput>
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/action-comment" replace color="info">
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
  actions: storeState.action.entities,
  actionCommentEntity: storeState.actionComment.entity,
  loading: storeState.actionComment.loading,
  updating: storeState.actionComment.updating,
  updateSuccess: storeState.actionComment.updateSuccess,
});

const mapDispatchToProps = {
  getActions,
  getEntity,
  updateEntity,
  createEntity,
  reset,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(ActionCommentUpdate);

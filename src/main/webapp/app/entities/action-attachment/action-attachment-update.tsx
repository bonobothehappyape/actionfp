import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { setFileData, openFile, byteSize, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IAction } from 'app/shared/model/action.model';
import { getEntities as getActions } from 'app/entities/action/action.reducer';
import { getEntity, updateEntity, createEntity, setBlob, reset } from './action-attachment.reducer';
import { IActionAttachment } from 'app/shared/model/action-attachment.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IActionAttachmentUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const ActionAttachmentUpdate = (props: IActionAttachmentUpdateProps) => {
  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const { actionAttachmentEntity, actions, loading, updating } = props;

  const { attachedFile, attachedFileContentType } = actionAttachmentEntity;

  const handleClose = () => {
    props.history.push('/action-attachment');
  };

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntity(props.match.params.id);
    }

    props.getActions();
  }, []);

  const onBlobChange = (isAnImage, name) => event => {
    setFileData(event, (contentType, data) => props.setBlob(name, data, contentType), isAnImage);
  };

  const clearBlob = name => () => {
    props.setBlob(name, undefined, undefined);
  };

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const entity = {
        ...actionAttachmentEntity,
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
          <h2 id="actionfpApp.actionAttachment.home.createOrEditLabel" data-cy="ActionAttachmentCreateUpdateHeading">
            Create or edit a ActionAttachment
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : actionAttachmentEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="action-attachment-id">Id</Label>
                  <AvInput id="action-attachment-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="nameLabel" for="action-attachment-name">
                  Name
                </Label>
                <AvField id="action-attachment-name" data-cy="name" type="text" name="name" />
              </AvGroup>
              <AvGroup>
                <Label id="mimeTypeLabel" for="action-attachment-mimeType">
                  Mime Type
                </Label>
                <AvField id="action-attachment-mimeType" data-cy="mimeType" type="text" name="mimeType" />
              </AvGroup>
              <AvGroup>
                <AvGroup>
                  <Label id="attachedFileLabel" for="attachedFile">
                    Attached File
                  </Label>
                  <br />
                  {attachedFile ? (
                    <div>
                      {attachedFileContentType ? <a onClick={openFile(attachedFileContentType, attachedFile)}>Open</a> : null}
                      <br />
                      <Row>
                        <Col md="11">
                          <span>
                            {attachedFileContentType}, {byteSize(attachedFile)}
                          </span>
                        </Col>
                        <Col md="1">
                          <Button color="danger" onClick={clearBlob('attachedFile')}>
                            <FontAwesomeIcon icon="times-circle" />
                          </Button>
                        </Col>
                      </Row>
                    </div>
                  ) : null}
                  <input id="file_attachedFile" data-cy="attachedFile" type="file" onChange={onBlobChange(false, 'attachedFile')} />
                  <AvInput type="hidden" name="attachedFile" value={attachedFile} />
                </AvGroup>
              </AvGroup>
              <AvGroup>
                <Label id="urlLabel" for="action-attachment-url">
                  Url
                </Label>
                <AvField id="action-attachment-url" data-cy="url" type="text" name="url" />
              </AvGroup>
              <AvGroup>
                <Label for="action-attachment-action">Action</Label>
                <AvInput id="action-attachment-action" data-cy="action" type="select" className="form-control" name="actionId">
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
              <Button tag={Link} id="cancel-save" to="/action-attachment" replace color="info">
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
  actionAttachmentEntity: storeState.actionAttachment.entity,
  loading: storeState.actionAttachment.loading,
  updating: storeState.actionAttachment.updating,
  updateSuccess: storeState.actionAttachment.updateSuccess,
});

const mapDispatchToProps = {
  getActions,
  getEntity,
  updateEntity,
  setBlob,
  createEntity,
  reset,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(ActionAttachmentUpdate);

import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { openFile, byteSize } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './action-attachment.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IActionAttachmentDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const ActionAttachmentDetail = (props: IActionAttachmentDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { actionAttachmentEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="actionAttachmentDetailsHeading">ActionAttachment</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">Id</span>
          </dt>
          <dd>{actionAttachmentEntity.id}</dd>
          <dt>
            <span id="name">Name</span>
          </dt>
          <dd>{actionAttachmentEntity.name}</dd>
          <dt>
            <span id="mimeType">Mime Type</span>
          </dt>
          <dd>{actionAttachmentEntity.mimeType}</dd>
          <dt>
            <span id="attachedFile">Attached File</span>
          </dt>
          <dd>
            {actionAttachmentEntity.attachedFile ? (
              <div>
                {actionAttachmentEntity.attachedFileContentType ? (
                  <a onClick={openFile(actionAttachmentEntity.attachedFileContentType, actionAttachmentEntity.attachedFile)}>Open&nbsp;</a>
                ) : null}
                <span>
                  {actionAttachmentEntity.attachedFileContentType}, {byteSize(actionAttachmentEntity.attachedFile)}
                </span>
              </div>
            ) : null}
          </dd>
          <dt>
            <span id="url">Url</span>
          </dt>
          <dd>{actionAttachmentEntity.url}</dd>
          <dt>Action</dt>
          <dd>{actionAttachmentEntity.action ? actionAttachmentEntity.action.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/action-attachment" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/action-attachment/${actionAttachmentEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ actionAttachment }: IRootState) => ({
  actionAttachmentEntity: actionAttachment.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(ActionAttachmentDetail);

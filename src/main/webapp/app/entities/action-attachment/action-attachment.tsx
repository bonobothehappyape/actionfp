import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, InputGroup, Col, Row, Table } from 'reactstrap';
import { AvForm, AvGroup, AvInput } from 'availity-reactstrap-validation';
import { openFile, byteSize, ICrudSearchAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getSearchEntities, getEntities } from './action-attachment.reducer';
import { IActionAttachment } from 'app/shared/model/action-attachment.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IActionAttachmentProps extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {}

export const ActionAttachment = (props: IActionAttachmentProps) => {
  const [search, setSearch] = useState('');

  useEffect(() => {
    props.getEntities();
  }, []);

  const startSearching = () => {
    if (search) {
      props.getSearchEntities(search);
    }
  };

  const clear = () => {
    setSearch('');
    props.getEntities();
  };

  const handleSearch = event => setSearch(event.target.value);

  const handleSyncList = () => {
    props.getEntities();
  };

  const { actionAttachmentList, match, loading } = props;
  return (
    <div>
      <h2 id="action-attachment-heading" data-cy="ActionAttachmentHeading">
        Action Attachments
        <div className="d-flex justify-content-end">
          <Button className="mr-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Refresh List
          </Button>
          <Link to={`${match.url}/new`} className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp; Create new Action Attachment
          </Link>
        </div>
      </h2>
      <Row>
        <Col sm="12">
          <AvForm onSubmit={startSearching}>
            <AvGroup>
              <InputGroup>
                <AvInput type="text" name="search" value={search} onChange={handleSearch} placeholder="Search" />
                <Button className="input-group-addon">
                  <FontAwesomeIcon icon="search" />
                </Button>
                <Button type="reset" className="input-group-addon" onClick={clear}>
                  <FontAwesomeIcon icon="trash" />
                </Button>
              </InputGroup>
            </AvGroup>
          </AvForm>
        </Col>
      </Row>
      <div className="table-responsive">
        {actionAttachmentList && actionAttachmentList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>Id</th>
                <th>Name</th>
                <th>Mime Type</th>
                <th>Attached File</th>
                <th>Url</th>
                <th>Action</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {actionAttachmentList.map((actionAttachment, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`${match.url}/${actionAttachment.id}`} color="link" size="sm">
                      {actionAttachment.id}
                    </Button>
                  </td>
                  <td>{actionAttachment.name}</td>
                  <td>{actionAttachment.mimeType}</td>
                  <td>
                    {actionAttachment.attachedFile ? (
                      <div>
                        {actionAttachment.attachedFileContentType ? (
                          <a onClick={openFile(actionAttachment.attachedFileContentType, actionAttachment.attachedFile)}>Open &nbsp;</a>
                        ) : null}
                        <span>
                          {actionAttachment.attachedFileContentType}, {byteSize(actionAttachment.attachedFile)}
                        </span>
                      </div>
                    ) : null}
                  </td>
                  <td>{actionAttachment.url}</td>
                  <td>
                    {actionAttachment.action ? <Link to={`action/${actionAttachment.action.id}`}>{actionAttachment.action.id}</Link> : ''}
                  </td>
                  <td className="text-right">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`${match.url}/${actionAttachment.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`${match.url}/${actionAttachment.id}/edit`}
                        color="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`${match.url}/${actionAttachment.id}/delete`}
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" /> <span className="d-none d-md-inline">Delete</span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && <div className="alert alert-warning">No Action Attachments found</div>
        )}
      </div>
    </div>
  );
};

const mapStateToProps = ({ actionAttachment }: IRootState) => ({
  actionAttachmentList: actionAttachment.entities,
  loading: actionAttachment.loading,
});

const mapDispatchToProps = {
  getSearchEntities,
  getEntities,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(ActionAttachment);

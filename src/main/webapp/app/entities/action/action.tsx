import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, InputGroup, Col, Row, Table } from 'reactstrap';
import { AvForm, AvGroup, AvInput } from 'availity-reactstrap-validation';
import { ICrudSearchAction, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getSearchEntities, getEntities } from './action.reducer';
import { IAction } from 'app/shared/model/action.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IActionProps extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {}

export const Action = (props: IActionProps) => {
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

  const { actionList, match, loading } = props;
  return (
    <div>
      <h2 id="action-heading" data-cy="ActionHeading">
        Actions
        <div className="d-flex justify-content-end">
          <Button className="mr-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Refresh List
          </Button>
          <Link to={`${match.url}/new`} className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp; Create new Action
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
        {actionList && actionList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>Id</th>
                <th>Task Name</th>
                <th>Task Description</th>
                <th>Requires Periodic Followup</th>
                <th>Initial Deadline</th>
                <th>Updated Deadline</th>
                <th>Done Date</th>
                <th>Verified Date</th>
                <th>Ics Recomm</th>
                <th>Owner Unit</th>
                <th>Status</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {actionList.map((action, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`${match.url}/${action.id}`} color="link" size="sm">
                      {action.id}
                    </Button>
                  </td>
                  <td>{action.taskName}</td>
                  <td>{action.taskDescription}</td>
                  <td>{action.requiresPeriodicFollowup ? 'true' : 'false'}</td>
                  <td>
                    {action.initialDeadline ? (
                      <TextFormat type="date" value={action.initialDeadline} format={APP_LOCAL_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    {action.updatedDeadline ? (
                      <TextFormat type="date" value={action.updatedDeadline} format={APP_LOCAL_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{action.doneDate ? <TextFormat type="date" value={action.doneDate} format={APP_LOCAL_DATE_FORMAT} /> : null}</td>
                  <td>
                    {action.verifiedDate ? <TextFormat type="date" value={action.verifiedDate} format={APP_LOCAL_DATE_FORMAT} /> : null}
                  </td>
                  <td>{action.icsRecomm ? <Link to={`ics-recomm/${action.icsRecomm.id}`}>{action.icsRecomm.id}</Link> : ''}</td>
                  <td>{action.ownerUnit ? <Link to={`unit/${action.ownerUnit.id}`}>{action.ownerUnit.id}</Link> : ''}</td>
                  <td>{action.status ? <Link to={`status/${action.status.id}`}>{action.status.id}</Link> : ''}</td>
                  <td className="text-right">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`${match.url}/${action.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${action.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${action.id}/delete`} color="danger" size="sm" data-cy="entityDeleteButton">
                        <FontAwesomeIcon icon="trash" /> <span className="d-none d-md-inline">Delete</span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && <div className="alert alert-warning">No Actions found</div>
        )}
      </div>
    </div>
  );
};

const mapStateToProps = ({ action }: IRootState) => ({
  actionList: action.entities,
  loading: action.loading,
});

const mapDispatchToProps = {
  getSearchEntities,
  getEntities,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(Action);

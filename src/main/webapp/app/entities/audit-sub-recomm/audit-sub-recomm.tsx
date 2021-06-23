import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, InputGroup, Col, Row, Table } from 'reactstrap';
import { AvForm, AvGroup, AvInput } from 'availity-reactstrap-validation';
import { ICrudSearchAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getSearchEntities, getEntities } from './audit-sub-recomm.reducer';
import { IAuditSubRecomm } from 'app/shared/model/audit-sub-recomm.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IAuditSubRecommProps extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {}

export const AuditSubRecomm = (props: IAuditSubRecommProps) => {
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

  const { auditSubRecommList, match, loading } = props;
  return (
    <div>
      <h2 id="audit-sub-recomm-heading" data-cy="AuditSubRecommHeading">
        Audit Sub Recomms
        <div className="d-flex justify-content-end">
          <Button className="mr-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Refresh List
          </Button>
          <Link to={`${match.url}/new`} className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp; Create new Audit Sub Recomm
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
        {auditSubRecommList && auditSubRecommList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>Id</th>
                <th>Sub Recomm Num</th>
                <th>Description</th>
                <th>Status</th>
                <th>Audit Recomm</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {auditSubRecommList.map((auditSubRecomm, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`${match.url}/${auditSubRecomm.id}`} color="link" size="sm">
                      {auditSubRecomm.id}
                    </Button>
                  </td>
                  <td>{auditSubRecomm.subRecommNum}</td>
                  <td>{auditSubRecomm.description}</td>
                  <td>{auditSubRecomm.status ? <Link to={`status/${auditSubRecomm.status.id}`}>{auditSubRecomm.status.id}</Link> : ''}</td>
                  <td>
                    {auditSubRecomm.auditRecomm ? (
                      <Link to={`audit-recomm/${auditSubRecomm.auditRecomm.id}`}>{auditSubRecomm.auditRecomm.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-right">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`${match.url}/${auditSubRecomm.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${auditSubRecomm.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`${match.url}/${auditSubRecomm.id}/delete`}
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
          !loading && <div className="alert alert-warning">No Audit Sub Recomms found</div>
        )}
      </div>
    </div>
  );
};

const mapStateToProps = ({ auditSubRecomm }: IRootState) => ({
  auditSubRecommList: auditSubRecomm.entities,
  loading: auditSubRecomm.loading,
});

const mapDispatchToProps = {
  getSearchEntities,
  getEntities,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(AuditSubRecomm);

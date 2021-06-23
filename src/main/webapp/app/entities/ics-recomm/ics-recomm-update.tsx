import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { getEntity, updateEntity, createEntity, reset } from './ics-recomm.reducer';
import { IICSRecomm } from 'app/shared/model/ics-recomm.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IICSRecommUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const ICSRecommUpdate = (props: IICSRecommUpdateProps) => {
  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const { iCSRecommEntity, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/ics-recomm');
  };

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntity(props.match.params.id);
    }
  }, []);

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const entity = {
        ...iCSRecommEntity,
        ...values,
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
          <h2 id="actionfpApp.iCSRecomm.home.createOrEditLabel" data-cy="ICSRecommCreateUpdateHeading">
            Create or edit a ICSRecomm
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : iCSRecommEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="ics-recomm-id">Id</Label>
                  <AvInput id="ics-recomm-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="yearLabel" for="ics-recomm-year">
                  Year
                </Label>
                <AvField id="ics-recomm-year" data-cy="year" type="string" className="form-control" name="year" />
              </AvGroup>
              <AvGroup>
                <Label id="icsNumberLabel" for="ics-recomm-icsNumber">
                  Ics Number
                </Label>
                <AvField id="ics-recomm-icsNumber" data-cy="icsNumber" type="text" name="icsNumber" />
              </AvGroup>
              <AvGroup>
                <Label id="icsDescrLabel" for="ics-recomm-icsDescr">
                  Ics Descr
                </Label>
                <AvField id="ics-recomm-icsDescr" data-cy="icsDescr" type="text" name="icsDescr" />
              </AvGroup>
              <AvGroup>
                <Label id="titleLabel" for="ics-recomm-title">
                  Title
                </Label>
                <AvField id="ics-recomm-title" data-cy="title" type="text" name="title" />
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/ics-recomm" replace color="info">
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
  iCSRecommEntity: storeState.iCSRecomm.entity,
  loading: storeState.iCSRecomm.loading,
  updating: storeState.iCSRecomm.updating,
  updateSuccess: storeState.iCSRecomm.updateSuccess,
});

const mapDispatchToProps = {
  getEntity,
  updateEntity,
  createEntity,
  reset,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(ICSRecommUpdate);

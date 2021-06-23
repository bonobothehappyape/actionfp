import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Unit from './unit';
import UnitDetail from './unit-detail';
import UnitUpdate from './unit-update';
import UnitDeleteDialog from './unit-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={UnitUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={UnitUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={UnitDetail} />
      <ErrorBoundaryRoute path={match.url} component={Unit} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={UnitDeleteDialog} />
  </>
);

export default Routes;

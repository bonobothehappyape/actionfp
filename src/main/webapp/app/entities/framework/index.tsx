import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Framework from './framework';
import FrameworkDetail from './framework-detail';
import FrameworkUpdate from './framework-update';
import FrameworkDeleteDialog from './framework-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={FrameworkUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={FrameworkUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={FrameworkDetail} />
      <ErrorBoundaryRoute path={match.url} component={Framework} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={FrameworkDeleteDialog} />
  </>
);

export default Routes;

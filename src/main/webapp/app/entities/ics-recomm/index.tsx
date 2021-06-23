import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import ICSRecomm from './ics-recomm';
import ICSRecommDetail from './ics-recomm-detail';
import ICSRecommUpdate from './ics-recomm-update';
import ICSRecommDeleteDialog from './ics-recomm-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={ICSRecommUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={ICSRecommUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={ICSRecommDetail} />
      <ErrorBoundaryRoute path={match.url} component={ICSRecomm} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={ICSRecommDeleteDialog} />
  </>
);

export default Routes;

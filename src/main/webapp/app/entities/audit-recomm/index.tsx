import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import AuditRecomm from './audit-recomm';
import AuditRecommDetail from './audit-recomm-detail';
import AuditRecommUpdate from './audit-recomm-update';
import AuditRecommDeleteDialog from './audit-recomm-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={AuditRecommUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={AuditRecommUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={AuditRecommDetail} />
      <ErrorBoundaryRoute path={match.url} component={AuditRecomm} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={AuditRecommDeleteDialog} />
  </>
);

export default Routes;

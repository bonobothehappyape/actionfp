import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import AuditSubRecomm from './audit-sub-recomm';
import AuditSubRecommDetail from './audit-sub-recomm-detail';
import AuditSubRecommUpdate from './audit-sub-recomm-update';
import AuditSubRecommDeleteDialog from './audit-sub-recomm-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={AuditSubRecommUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={AuditSubRecommUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={AuditSubRecommDetail} />
      <ErrorBoundaryRoute path={match.url} component={AuditSubRecomm} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={AuditSubRecommDeleteDialog} />
  </>
);

export default Routes;

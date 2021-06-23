import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import AuditReport from './audit-report';
import AuditReportDetail from './audit-report-detail';
import AuditReportUpdate from './audit-report-update';
import AuditReportDeleteDialog from './audit-report-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={AuditReportUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={AuditReportUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={AuditReportDetail} />
      <ErrorBoundaryRoute path={match.url} component={AuditReport} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={AuditReportDeleteDialog} />
  </>
);

export default Routes;

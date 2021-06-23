import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import ActionChangeMail from './action-change-mail';
import ActionChangeMailDetail from './action-change-mail-detail';
import ActionChangeMailUpdate from './action-change-mail-update';
import ActionChangeMailDeleteDialog from './action-change-mail-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={ActionChangeMailUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={ActionChangeMailUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={ActionChangeMailDetail} />
      <ErrorBoundaryRoute path={match.url} component={ActionChangeMail} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={ActionChangeMailDeleteDialog} />
  </>
);

export default Routes;

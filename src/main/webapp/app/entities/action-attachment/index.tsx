import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import ActionAttachment from './action-attachment';
import ActionAttachmentDetail from './action-attachment-detail';
import ActionAttachmentUpdate from './action-attachment-update';
import ActionAttachmentDeleteDialog from './action-attachment-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={ActionAttachmentUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={ActionAttachmentUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={ActionAttachmentDetail} />
      <ErrorBoundaryRoute path={match.url} component={ActionAttachment} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={ActionAttachmentDeleteDialog} />
  </>
);

export default Routes;

import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import ActionComment from './action-comment';
import ActionCommentDetail from './action-comment-detail';
import ActionCommentUpdate from './action-comment-update';
import ActionCommentDeleteDialog from './action-comment-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={ActionCommentUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={ActionCommentUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={ActionCommentDetail} />
      <ErrorBoundaryRoute path={match.url} component={ActionComment} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={ActionCommentDeleteDialog} />
  </>
);

export default Routes;

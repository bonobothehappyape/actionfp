import React from 'react';
import { Switch } from 'react-router-dom';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Unit from './unit';
import Framework from './framework';
import ActionComment from './action-comment';
import Status from './status';
import ActionAttachment from './action-attachment';
import AuditReport from './audit-report';
import AuditRecomm from './audit-recomm';
import ICSRecomm from './ics-recomm';
import AuditSubRecomm from './audit-sub-recomm';
import Action from './action';
import ActionChangeMail from './action-change-mail';
/* jhipster-needle-add-route-import - JHipster will add routes here */

const Routes = ({ match }) => (
  <div>
    <Switch>
      {/* prettier-ignore */}
      <ErrorBoundaryRoute path={`${match.url}unit`} component={Unit} />
      <ErrorBoundaryRoute path={`${match.url}framework`} component={Framework} />
      <ErrorBoundaryRoute path={`${match.url}action-comment`} component={ActionComment} />
      <ErrorBoundaryRoute path={`${match.url}status`} component={Status} />
      <ErrorBoundaryRoute path={`${match.url}action-attachment`} component={ActionAttachment} />
      <ErrorBoundaryRoute path={`${match.url}audit-report`} component={AuditReport} />
      <ErrorBoundaryRoute path={`${match.url}audit-recomm`} component={AuditRecomm} />
      <ErrorBoundaryRoute path={`${match.url}ics-recomm`} component={ICSRecomm} />
      <ErrorBoundaryRoute path={`${match.url}audit-sub-recomm`} component={AuditSubRecomm} />
      <ErrorBoundaryRoute path={`${match.url}action`} component={Action} />
      <ErrorBoundaryRoute path={`${match.url}action-change-mail`} component={ActionChangeMail} />
      {/* jhipster-needle-add-route-path - JHipster will add routes here */}
    </Switch>
  </div>
);

export default Routes;

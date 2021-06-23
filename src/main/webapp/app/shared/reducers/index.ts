import { combineReducers } from 'redux';
import { loadingBarReducer as loadingBar } from 'react-redux-loading-bar';

import authentication, { AuthenticationState } from './authentication';
import applicationProfile, { ApplicationProfileState } from './application-profile';

import administration, { AdministrationState } from 'app/modules/administration/administration.reducer';
import userManagement, { UserManagementState } from './user-management';
// prettier-ignore
import unit, {
  UnitState
} from 'app/entities/unit/unit.reducer';
// prettier-ignore
import framework, {
  FrameworkState
} from 'app/entities/framework/framework.reducer';
// prettier-ignore
import actionComment, {
  ActionCommentState
} from 'app/entities/action-comment/action-comment.reducer';
// prettier-ignore
import status, {
  StatusState
} from 'app/entities/status/status.reducer';
// prettier-ignore
import actionAttachment, {
  ActionAttachmentState
} from 'app/entities/action-attachment/action-attachment.reducer';
// prettier-ignore
import auditReport, {
  AuditReportState
} from 'app/entities/audit-report/audit-report.reducer';
// prettier-ignore
import auditRecomm, {
  AuditRecommState
} from 'app/entities/audit-recomm/audit-recomm.reducer';
// prettier-ignore
import iCSRecomm, {
  ICSRecommState
} from 'app/entities/ics-recomm/ics-recomm.reducer';
// prettier-ignore
import auditSubRecomm, {
  AuditSubRecommState
} from 'app/entities/audit-sub-recomm/audit-sub-recomm.reducer';
// prettier-ignore
import action, {
  ActionState
} from 'app/entities/action/action.reducer';
// prettier-ignore
import actionChangeMail, {
  ActionChangeMailState
} from 'app/entities/action-change-mail/action-change-mail.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

export interface IRootState {
  readonly authentication: AuthenticationState;
  readonly applicationProfile: ApplicationProfileState;
  readonly administration: AdministrationState;
  readonly userManagement: UserManagementState;
  readonly unit: UnitState;
  readonly framework: FrameworkState;
  readonly actionComment: ActionCommentState;
  readonly status: StatusState;
  readonly actionAttachment: ActionAttachmentState;
  readonly auditReport: AuditReportState;
  readonly auditRecomm: AuditRecommState;
  readonly iCSRecomm: ICSRecommState;
  readonly auditSubRecomm: AuditSubRecommState;
  readonly action: ActionState;
  readonly actionChangeMail: ActionChangeMailState;
  /* jhipster-needle-add-reducer-type - JHipster will add reducer type here */
  readonly loadingBar: any;
}

const rootReducer = combineReducers<IRootState>({
  authentication,
  applicationProfile,
  administration,
  userManagement,
  unit,
  framework,
  actionComment,
  status,
  actionAttachment,
  auditReport,
  auditRecomm,
  iCSRecomm,
  auditSubRecomm,
  action,
  actionChangeMail,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
  loadingBar,
});

export default rootReducer;

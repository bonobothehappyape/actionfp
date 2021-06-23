import { IStatus } from 'app/shared/model/status.model';
import { IAuditRecomm } from 'app/shared/model/audit-recomm.model';

export interface IAuditSubRecomm {
  id?: number;
  subRecommNum?: string | null;
  description?: string | null;
  status?: IStatus | null;
  auditRecomm?: IAuditRecomm | null;
}

export const defaultValue: Readonly<IAuditSubRecomm> = {};

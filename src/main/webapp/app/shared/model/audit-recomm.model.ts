import { IAuditReport } from 'app/shared/model/audit-report.model';
import { IStatus } from 'app/shared/model/status.model';

export interface IAuditRecomm {
  id?: number;
  recommNumber?: string | null;
  priority?: number | null;
  description?: string | null;
  report?: IAuditReport | null;
  status?: IStatus | null;
}

export const defaultValue: Readonly<IAuditRecomm> = {};

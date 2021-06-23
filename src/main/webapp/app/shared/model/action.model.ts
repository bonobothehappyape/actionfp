import dayjs from 'dayjs';
import { IFramework } from 'app/shared/model/framework.model';
import { IICSRecomm } from 'app/shared/model/ics-recomm.model';
import { IUnit } from 'app/shared/model/unit.model';
import { IStatus } from 'app/shared/model/status.model';

export interface IAction {
  id?: number;
  taskName?: string | null;
  taskDescription?: string | null;
  requiresPeriodicFollowup?: boolean | null;
  initialDeadline?: string | null;
  updatedDeadline?: string | null;
  doneDate?: string | null;
  verifiedDate?: string | null;
  frameworks?: IFramework[] | null;
  icsRecomm?: IICSRecomm | null;
  ownerUnit?: IUnit | null;
  status?: IStatus | null;
}

export const defaultValue: Readonly<IAction> = {
  requiresPeriodicFollowup: false,
};

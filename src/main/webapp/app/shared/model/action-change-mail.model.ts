import { IAction } from 'app/shared/model/action.model';
import { IUser } from 'app/shared/model/user.model';

export interface IActionChangeMail {
  id?: number;
  actionType?: string | null;
  action?: IAction | null;
  user?: IUser | null;
}

export const defaultValue: Readonly<IActionChangeMail> = {};

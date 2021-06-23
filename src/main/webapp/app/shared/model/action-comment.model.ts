import { IAction } from 'app/shared/model/action.model';

export interface IActionComment {
  id?: number;
  comment?: string | null;
  action?: IAction | null;
}

export const defaultValue: Readonly<IActionComment> = {};

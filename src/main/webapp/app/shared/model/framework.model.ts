import { IUnit } from 'app/shared/model/unit.model';
import { IAction } from 'app/shared/model/action.model';

export interface IFramework {
  id?: number;
  year?: number | null;
  name?: string | null;
  type?: number | null;
  description?: string | null;
  unit?: IUnit | null;
  action?: IAction | null;
}

export const defaultValue: Readonly<IFramework> = {};

import { IAction } from 'app/shared/model/action.model';

export interface IActionAttachment {
  id?: number;
  name?: string | null;
  mimeType?: string | null;
  attachedFileContentType?: string | null;
  attachedFile?: string | null;
  url?: string | null;
  action?: IAction | null;
}

export const defaultValue: Readonly<IActionAttachment> = {};

export interface IICSRecomm {
  id?: number;
  year?: number | null;
  icsNumber?: string | null;
  icsDescr?: string | null;
  title?: string | null;
}

export const defaultValue: Readonly<IICSRecomm> = {};

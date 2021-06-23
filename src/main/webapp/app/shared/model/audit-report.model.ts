export interface IAuditReport {
  id?: number;
  year?: number | null;
  reportTitle?: string | null;
  institution?: number | null;
  reportDescription?: string | null;
}

export const defaultValue: Readonly<IAuditReport> = {};

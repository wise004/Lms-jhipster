import dayjs from 'dayjs';
import { IEnrollment } from 'app/shared/model/enrollment.model';
import { CertificateStatus } from 'app/shared/model/enumerations/certificate-status.model';

export interface ICertificate {
  id?: number;
  url?: string;
  issuedAt?: dayjs.Dayjs;
  status?: keyof typeof CertificateStatus;
  enrollment?: IEnrollment | null;
}

export const defaultValue: Readonly<ICertificate> = {};

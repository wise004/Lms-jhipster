import dayjs from 'dayjs';
import { ICourse } from 'app/shared/model/course.model';
import { IAppUser } from 'app/shared/model/app-user.model';
import { EnrollmentStatus } from 'app/shared/model/enumerations/enrollment-status.model';
import { PaymentStatus } from 'app/shared/model/enumerations/payment-status.model';

export interface IEnrollment {
  id?: number;
  enrollmentDate?: dayjs.Dayjs | null;
  progressPercentage?: number | null;
  progress?: string | null;
  lastAccessedAt?: dayjs.Dayjs | null;
  status?: keyof typeof EnrollmentStatus | null;
  paymentStatus?: keyof typeof PaymentStatus | null;
  amountPaid?: number | null;
  transactionId?: string | null;
  completedAt?: dayjs.Dayjs | null;
  course?: ICourse | null;
  student?: IAppUser | null;
}

export const defaultValue: Readonly<IEnrollment> = {};

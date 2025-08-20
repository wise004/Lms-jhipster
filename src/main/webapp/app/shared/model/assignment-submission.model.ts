import dayjs from 'dayjs';
import { IAssignment } from 'app/shared/model/assignment.model';
import { IAppUser } from 'app/shared/model/app-user.model';
import { SubmissionStatus } from 'app/shared/model/enumerations/submission-status.model';

export interface IAssignmentSubmission {
  id?: number;
  submittedAt?: dayjs.Dayjs | null;
  submissionText?: string | null;
  fileUrls?: string | null;
  grade?: number | null;
  feedback?: string | null;
  status?: keyof typeof SubmissionStatus | null;
  assignment?: IAssignment | null;
  student?: IAppUser | null;
}

export const defaultValue: Readonly<IAssignmentSubmission> = {};

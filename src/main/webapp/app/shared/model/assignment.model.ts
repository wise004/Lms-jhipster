import dayjs from 'dayjs';
import { ICourse } from 'app/shared/model/course.model';
import { ILesson } from 'app/shared/model/lesson.model';

export interface IAssignment {
  id?: number;
  title?: string;
  description?: string | null;
  instructions?: string | null;
  dueDate?: dayjs.Dayjs | null;
  maxPoints?: number | null;
  submissionType?: string | null;
  allowedFileTypes?: string | null;
  maxFileSize?: number | null;
  isPublished?: boolean | null;
  allowLateSubmission?: boolean | null;
  lateSubmissionPenalty?: number | null;
  sortOrder?: number | null;
  course?: ICourse | null;
  lesson?: ILesson | null;
}

export const defaultValue: Readonly<IAssignment> = {
  isPublished: false,
  allowLateSubmission: false,
};

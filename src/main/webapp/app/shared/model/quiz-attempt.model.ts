import dayjs from 'dayjs';
import { IQuiz } from 'app/shared/model/quiz.model';
import { IAppUser } from 'app/shared/model/app-user.model';
import { AttemptStatus } from 'app/shared/model/enumerations/attempt-status.model';

export interface IQuizAttempt {
  id?: number;
  startedAt?: dayjs.Dayjs | null;
  submittedAt?: dayjs.Dayjs | null;
  score?: number | null;
  passed?: boolean | null;
  answers?: string | null;
  attemptNumber?: number | null;
  status?: keyof typeof AttemptStatus | null;
  quiz?: IQuiz | null;
  student?: IAppUser | null;
}

export const defaultValue: Readonly<IQuizAttempt> = {
  passed: false,
};

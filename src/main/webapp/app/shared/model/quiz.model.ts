import dayjs from 'dayjs';
import { ICourse } from 'app/shared/model/course.model';
import { ILesson } from 'app/shared/model/lesson.model';

export interface IQuiz {
  id?: number;
  title?: string;
  description?: string | null;
  timeLimit?: number | null;
  passingScore?: number | null;
  attemptsAllowed?: number | null;
  sortOrder?: number | null;
  questions?: string | null;
  isPublished?: boolean | null;
  availableFrom?: dayjs.Dayjs | null;
  availableUntil?: dayjs.Dayjs | null;
  course?: ICourse | null;
  lesson?: ILesson | null;
}

export const defaultValue: Readonly<IQuiz> = {
  isPublished: false,
};

import { ICourse } from 'app/shared/model/course.model';
import { LessonType } from 'app/shared/model/enumerations/lesson-type.model';

export interface ILesson {
  id?: number;
  title?: string;
  description?: string | null;
  content?: string | null;
  videoUrl?: string | null;
  duration?: number | null;
  type?: keyof typeof LessonType;
  isFree?: boolean | null;
  sortOrder?: number | null;
  isPublished?: boolean | null;
  course?: ICourse | null;
}

export const defaultValue: Readonly<ILesson> = {
  isFree: false,
  isPublished: false,
};

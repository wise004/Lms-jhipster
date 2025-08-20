import dayjs from 'dayjs';
import { ILesson } from 'app/shared/model/lesson.model';
import { IAppUser } from 'app/shared/model/app-user.model';
import { CommentStatus } from 'app/shared/model/enumerations/comment-status.model';

export interface IComment {
  id?: number;
  content?: string | null;
  createdAt?: dayjs.Dayjs;
  updatedAt?: dayjs.Dayjs | null;
  status?: keyof typeof CommentStatus;
  lesson?: ILesson | null;
  author?: IAppUser | null;
  parent?: IComment | null;
}

export const defaultValue: Readonly<IComment> = {};

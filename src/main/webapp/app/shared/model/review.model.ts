import dayjs from 'dayjs';
import { ICourse } from 'app/shared/model/course.model';
import { IAppUser } from 'app/shared/model/app-user.model';

export interface IReview {
  id?: number;
  rating?: number;
  reviewText?: string | null;
  createdAt?: dayjs.Dayjs;
  course?: ICourse | null;
  student?: IAppUser | null;
}

export const defaultValue: Readonly<IReview> = {};

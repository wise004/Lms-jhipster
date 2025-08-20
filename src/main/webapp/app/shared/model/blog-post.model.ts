import dayjs from 'dayjs';
import { IAppUser } from 'app/shared/model/app-user.model';
import { PostStatus } from 'app/shared/model/enumerations/post-status.model';

export interface IBlogPost {
  id?: number;
  title?: string;
  slug?: string;
  summary?: string | null;
  content?: string | null;
  coverImageUrl?: string | null;
  publishDate?: dayjs.Dayjs | null;
  status?: keyof typeof PostStatus;
  author?: IAppUser | null;
}

export const defaultValue: Readonly<IBlogPost> = {};

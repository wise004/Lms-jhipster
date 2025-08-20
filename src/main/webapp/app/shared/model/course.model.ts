import { IAppUser } from 'app/shared/model/app-user.model';
import { ICategory } from 'app/shared/model/category.model';
import { CourseStatus } from 'app/shared/model/enumerations/course-status.model';

export interface ICourse {
  id?: number;
  title?: string;
  slug?: string;
  shortDescription?: string | null;
  description?: string | null;
  thumbnailUrl?: string | null;
  price?: number;
  originalPrice?: number | null;
  level?: string | null;
  language?: string | null;
  status?: keyof typeof CourseStatus;
  isPublished?: boolean;
  isFeatured?: boolean | null;
  averageRating?: number | null;
  enrollmentCount?: number | null;
  instructor?: IAppUser | null;
  category?: ICategory | null;
}

export const defaultValue: Readonly<ICourse> = {
  isPublished: false,
  isFeatured: false,
};

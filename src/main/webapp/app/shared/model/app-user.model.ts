import { Role } from 'app/shared/model/enumerations/role.model';

export interface IAppUser {
  id?: number;
  role?: keyof typeof Role;
  firstName?: string | null;
  lastName?: string | null;
  email?: string;
  phone?: string | null;
  bio?: string | null;
  profilePictureUrl?: string | null;
  isActive?: boolean;
}

export const defaultValue: Readonly<IAppUser> = {
  isActive: false,
};

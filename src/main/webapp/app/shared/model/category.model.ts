export interface ICategory {
  id?: number;
  name?: string;
  slug?: string;
  color?: string | null;
}

export const defaultValue: Readonly<ICategory> = {};

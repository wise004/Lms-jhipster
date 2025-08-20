import axios from 'axios';
import { createAsyncThunk, isFulfilled, isPending } from '@reduxjs/toolkit';
import { cleanEntity } from 'app/shared/util/entity-utils';
import { EntityState, IQueryParams, createEntitySlice, serializeAxiosError } from 'app/shared/reducers/reducer.utils';
import { IEnrollment, defaultValue } from 'app/shared/model/enrollment.model';

const initialState: EntityState<IEnrollment> = {
  loading: false,
  errorMessage: null,
  entities: [],
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};

const apiUrl = 'api/enrollments';
const myApiUrl = 'api/enrollments/mine';

// Actions

export const getEntities = createAsyncThunk(
  'enrollment/fetch_entity_list',
  async ({ page, size, sort }: IQueryParams) => {
    const requestUrl = `${apiUrl}?${sort ? `page=${page}&size=${size}&sort=${sort}&` : ''}cacheBuster=${new Date().getTime()}`;
    return axios.get<IEnrollment[]>(requestUrl);
  },
  { serializeError: serializeAxiosError },
);

export const getEntity = createAsyncThunk(
  'enrollment/fetch_entity',
  async (id: string | number) => {
    const requestUrl = `${apiUrl}/${id}`;
    return axios.get<IEnrollment>(requestUrl);
  },
  { serializeError: serializeAxiosError },
);

// Fetch current user's enrollment for a given courseId (returns array, we pick first)
export const getMyEnrollmentForCourse = createAsyncThunk(
  'enrollment/fetch_my_for_course',
  async (courseId: number) => {
    const requestUrl = `${myApiUrl}?courseId.equals=${courseId}&cacheBuster=${new Date().getTime()}`;
    return axios.get<IEnrollment[]>(requestUrl);
  },
  { serializeError: serializeAxiosError },
);

export const createEntity = createAsyncThunk(
  'enrollment/create_entity',
  async (entity: IEnrollment, thunkAPI) => {
    const result = await axios.post<IEnrollment>(apiUrl, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError },
);

// Create enrollment for a course (student inferred server-side)
export const createForCourse = createAsyncThunk(
  'enrollment/create_for_course',
  async (courseId: number) => {
    const payload: Partial<IEnrollment> = { course: { id: courseId } as any };
    return axios.post<IEnrollment>(apiUrl, payload);
  },
  { serializeError: serializeAxiosError },
);

export const updateEntity = createAsyncThunk(
  'enrollment/update_entity',
  async (entity: IEnrollment, thunkAPI) => {
    const result = await axios.put<IEnrollment>(`${apiUrl}/${entity.id}`, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError },
);

export const partialUpdateEntity = createAsyncThunk(
  'enrollment/partial_update_entity',
  async (entity: IEnrollment, thunkAPI) => {
    const result = await axios.patch<IEnrollment>(`${apiUrl}/${entity.id}`, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError },
);

// Update progress for an enrollment id
export const updateProgressFor = createAsyncThunk(
  'enrollment/update_progress_for',
  async ({ id, progress, percent }: { id: number; progress?: string | null; percent?: number | null }) => {
    return axios.post<IEnrollment>(`${apiUrl}/${id}/progress`, { progress, percent });
  },
  { serializeError: serializeAxiosError },
);

export const deleteEntity = createAsyncThunk(
  'enrollment/delete_entity',
  async (id: string | number, thunkAPI) => {
    const requestUrl = `${apiUrl}/${id}`;
    const result = await axios.delete<IEnrollment>(requestUrl);
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError },
);

// slice

export const EnrollmentSlice = createEntitySlice({
  name: 'enrollment',
  initialState,
  extraReducers(builder) {
    builder
      .addCase(getEntity.fulfilled, (state, action) => {
        state.loading = false;
        state.entity = action.payload.data;
      })
      .addCase(deleteEntity.fulfilled, state => {
        state.updating = false;
        state.updateSuccess = true;
        state.entity = {};
      })
      .addMatcher(isFulfilled(getEntities), (state, action) => {
        const { data, headers } = action.payload;

        return {
          ...state,
          loading: false,
          entities: data,
          totalItems: parseInt(headers['x-total-count'], 10),
        };
      })
      .addMatcher(isFulfilled(createEntity, updateEntity, partialUpdateEntity, createForCourse, updateProgressFor), (state, action) => {
        state.updating = false;
        state.loading = false;
        state.updateSuccess = true;
        state.entity = action.payload.data;
      })
      .addMatcher(isFulfilled(getMyEnrollmentForCourse), (state, action) => {
        state.loading = false;
        const list = action.payload.data || [];
        state.entity = list.length ? list[0] : ({} as any);
      })
      .addMatcher(isPending(getEntities, getEntity, getMyEnrollmentForCourse), state => {
        state.errorMessage = null;
        state.updateSuccess = false;
        state.loading = true;
      })
      .addMatcher(isPending(createEntity, updateEntity, partialUpdateEntity, deleteEntity, createForCourse, updateProgressFor), state => {
        state.errorMessage = null;
        state.updateSuccess = false;
        state.updating = true;
      });
  },
});

export const { reset } = EnrollmentSlice.actions;

// Reducer
export default EnrollmentSlice.reducer;

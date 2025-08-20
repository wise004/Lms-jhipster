import axios from 'axios';
import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';

const initialState = {
  loading: false,
  registrationSuccess: false,
  registrationFailure: false,
  errorMessage: null,
  successMessage: null,
};

export interface IUserAccount {
  login: string;
  email: string;
  password: string;
  langKey: string;
}

// Async actions
export const handleRegister = createAsyncThunk(
  'register/create_account',
  async (account: IUserAccount) => axios.post('api/register', account),
  {
    serializeError: (x: any) => x.data,
  },
);

export const RegisterSlice = createSlice({
  name: 'register',
  initialState: initialState as any,
  reducers: {
    reset() {
      return {
        ...initialState,
      };
    },
  },
  extraReducers(builder) {
    builder
      .addCase(handleRegister.pending, state => {
        state.loading = true;
        state.errorMessage = null;
        state.successMessage = null;
      })
      .addCase(handleRegister.rejected, (state, action) => {
        state.loading = false;
        state.registrationFailure = true;
        state.registrationSuccess = false;

        if (action.error?.message) {
          if (action.error.message.includes('Login name already registered')) {
            state.errorMessage = 'Login name already registered! Please choose another one.';
          } else if (action.error.message.includes('Email is already in use')) {
            state.errorMessage = 'Email is already in use! Please choose another one.';
          } else {
            state.errorMessage = 'Registration failed! Please try again later.';
          }
        } else {
          state.errorMessage = 'Registration failed! Please try again later.';
        }
      })
      .addCase(handleRegister.fulfilled, state => {
        state.loading = false;
        state.registrationFailure = false;
        state.registrationSuccess = true;
        state.successMessage = 'Registration saved! Please check your email for confirmation.';
        state.errorMessage = null;
      });
  },
});

export const { reset } = RegisterSlice.actions;

// Reducer
export default RegisterSlice.reducer;

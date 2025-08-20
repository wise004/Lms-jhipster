import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import QuizAttempt from './quiz-attempt';
import QuizAttemptDetail from './quiz-attempt-detail';
import QuizAttemptUpdate from './quiz-attempt-update';
import QuizAttemptDeleteDialog from './quiz-attempt-delete-dialog';

const QuizAttemptRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<QuizAttempt />} />
    <Route path="new" element={<QuizAttemptUpdate />} />
    <Route path=":id">
      <Route index element={<QuizAttemptDetail />} />
      <Route path="edit" element={<QuizAttemptUpdate />} />
      <Route path="delete" element={<QuizAttemptDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default QuizAttemptRoutes;

import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import AppUser from './app-user';
import Category from './category';
import Course from './course';
import Lesson from './lesson';
import Quiz from './quiz';
import Assignment from './assignment';
import Enrollment from './enrollment';
import AssignmentSubmission from './assignment-submission';
import QuizAttempt from './quiz-attempt';
import Certificate from './certificate';
import Review from './review';
import Comment from './comment';
import BlogPost from './blog-post';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="app-user/*" element={<AppUser />} />
        <Route path="category/*" element={<Category />} />
        <Route path="course/*" element={<Course />} />
        <Route path="lesson/*" element={<Lesson />} />
        <Route path="quiz/*" element={<Quiz />} />
        <Route path="assignment/*" element={<Assignment />} />
        <Route path="enrollment/*" element={<Enrollment />} />
        <Route path="assignment-submission/*" element={<AssignmentSubmission />} />
        <Route path="quiz-attempt/*" element={<QuizAttempt />} />
        <Route path="certificate/*" element={<Certificate />} />
        <Route path="review/*" element={<Review />} />
        <Route path="comment/*" element={<Comment />} />
        <Route path="blog-post/*" element={<BlogPost />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};

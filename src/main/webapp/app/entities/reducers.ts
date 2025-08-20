import appUser from 'app/entities/app-user/app-user.reducer';
import category from 'app/entities/category/category.reducer';
import course from 'app/entities/course/course.reducer';
import lesson from 'app/entities/lesson/lesson.reducer';
import quiz from 'app/entities/quiz/quiz.reducer';
import assignment from 'app/entities/assignment/assignment.reducer';
import enrollment from 'app/entities/enrollment/enrollment.reducer';
import assignmentSubmission from 'app/entities/assignment-submission/assignment-submission.reducer';
import quizAttempt from 'app/entities/quiz-attempt/quiz-attempt.reducer';
import certificate from 'app/entities/certificate/certificate.reducer';
import review from 'app/entities/review/review.reducer';
import comment from 'app/entities/comment/comment.reducer';
import blogPost from 'app/entities/blog-post/blog-post.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  appUser,
  category,
  course,
  lesson,
  quiz,
  assignment,
  enrollment,
  assignmentSubmission,
  quizAttempt,
  certificate,
  review,
  comment,
  blogPost,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;

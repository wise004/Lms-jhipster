import React, { useEffect, useMemo } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Alert, Button, Col, Row } from 'reactstrap';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntity as getLesson } from 'app/entities/lesson/lesson.reducer';
import { getByCourseId as getLessonsForCourse } from 'app/entities/lesson/lesson.reducer';
import { getMyEnrollmentForCourse, updateProgressFor } from 'app/entities/enrollment/enrollment.reducer';

const isMp4 = (url?: string | null) => !!url && /\.(mp4)(\?.*)?$/i.test(url);

const LessonPlayer = () => {
  const { id } = useParams();
  const dispatch = useAppDispatch();
  const auth = useAppSelector(state => state.authentication);
  const lessonState = useAppSelector(state => state.lesson);
  const enrollment = useAppSelector(state => state.enrollment.entity);
  const lesson = lessonState.entity;
  const lessons = lessonState.entities;

  useEffect(() => {
    if (id) dispatch(getLesson(id));
  }, [dispatch, id]);

  useEffect(() => {
    if (lesson?.course?.id) {
      dispatch(getLessonsForCourse(lesson.course.id));
      if (auth.isAuthenticated) dispatch(getMyEnrollmentForCourse(lesson.course.id));
    }
  }, [dispatch, auth.isAuthenticated, lesson?.course?.id]);

  const index = useMemo(() => {
    if (!lessons?.length || !lesson?.id) return -1;
    return lessons.findIndex(l => l.id === lesson.id);
  }, [lessons, lesson?.id]);

  const prevId = index > 0 ? lessons[index - 1]?.id : undefined;
  const nextId = index >= 0 && index < lessons.length - 1 ? lessons[index + 1]?.id : undefined;

  useEffect(() => {
    if (auth.isAuthenticated && enrollment?.id && lesson?.id) {
      try {
        const currentProgress = enrollment.progress ? JSON.parse(enrollment.progress) : {};
        const body = { ...currentProgress, lastLessonId: lesson.id };
        dispatch(updateProgressFor({ id: enrollment.id, progress: JSON.stringify(body), percent: enrollment.progressPercentage ?? 0 }));
      } catch (_) {
        // ignore JSON errors
      }
    }
  }, [dispatch, auth.isAuthenticated, enrollment?.id, lesson?.id]);

  const onMarkComplete = async () => {
    if (!auth.isAuthenticated || !enrollment?.id || !lesson?.id) return;
    const total = lessons?.length || 0;
    try {
      const current = enrollment.progress ? JSON.parse(enrollment.progress) : {};
      const completed: number[] = Array.isArray(current.completedLessonIds) ? current.completedLessonIds : [];
      const set = new Set<number>(completed);
      set.add(lesson.id);
      const completedArr = Array.from(set);
      const percent = total > 0 ? Math.round((completedArr.length / total) * 100) : 0;
      const body = { ...current, lastLessonId: lesson.id, completedLessonIds: completedArr };
      await dispatch(updateProgressFor({ id: enrollment.id, progress: JSON.stringify(body), percent }));
    } catch (_) {
      // ignore
    }
  };

  if (!lesson || !lesson.id) return <div className="container py-4">Loading…</div>;

  return (
    <div className="container py-4">
      <Row>
        <Col md="8">
          <h1 className="h4 mb-3">{lesson.title}</h1>
          {lesson.videoUrl ? (
            isMp4(lesson.videoUrl) ? (
              <video key={lesson.videoUrl} src={lesson.videoUrl} controls className="w-100 mb-3" />
            ) : (
              <div className="ratio ratio-16x9 mb-3">
                <iframe title={lesson.title || 'Lesson'} src={lesson.videoUrl} allowFullScreen />
              </div>
            )
          ) : lesson.content ? (
            <div className="mb-3" dangerouslySetInnerHTML={{ __html: lesson.content }} />
          ) : (
            <Alert color="info">This lesson has no content yet.</Alert>
          )}

          <div className="d-flex gap-2 mt-3">
            {prevId && (
              <Link to={`/lesson/${prevId}`} className="btn btn-outline-secondary">
                Previous lesson
              </Link>
            )}
            {nextId && (
              <Link to={`/lesson/${nextId}`} className="btn btn-outline-secondary">
                Next lesson
              </Link>
            )}
            {auth.isAuthenticated && enrollment?.id && (
              <Button color="primary" onClick={onMarkComplete} className="ms-auto">
                Mark as complete
              </Button>
            )}
          </div>
        </Col>
        <Col md="4">
          <div className="p-3 bg-white border rounded-3 shadow-sm">
            <div className="fw-semibold mb-2">Lessons</div>
            <ul className="list-unstyled mb-0">
              {lessons?.map(l => (
                <li key={l.id} className={`py-1 ${l.id === lesson.id ? 'fw-bold' : ''}`}>
                  <Link to={`/lesson/${l.id}`}>{l.title}</Link>
                </li>
              ))}
            </ul>
          </div>
        </Col>
      </Row>
    </div>
  );
};

export default LessonPlayer;

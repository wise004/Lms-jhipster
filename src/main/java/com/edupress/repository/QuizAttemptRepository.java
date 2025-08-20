package com.edupress.repository;

import com.edupress.domain.QuizAttempt;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the QuizAttempt entity.
 */
@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long>, JpaSpecificationExecutor<QuizAttempt> {
    default Optional<QuizAttempt> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<QuizAttempt> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<QuizAttempt> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select quizAttempt from QuizAttempt quizAttempt left join fetch quizAttempt.student",
        countQuery = "select count(quizAttempt) from QuizAttempt quizAttempt"
    )
    Page<QuizAttempt> findAllWithToOneRelationships(Pageable pageable);

    @Query("select quizAttempt from QuizAttempt quizAttempt left join fetch quizAttempt.student")
    List<QuizAttempt> findAllWithToOneRelationships();

    @Query("select quizAttempt from QuizAttempt quizAttempt left join fetch quizAttempt.student where quizAttempt.id =:id")
    Optional<QuizAttempt> findOneWithToOneRelationships(@Param("id") Long id);
}

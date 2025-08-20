package com.edupress.repository;

import com.edupress.domain.Quiz;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Quiz entity.
 */
@SuppressWarnings("unused")
@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long>, JpaSpecificationExecutor<Quiz> {}

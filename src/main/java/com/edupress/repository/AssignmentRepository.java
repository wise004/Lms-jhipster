package com.edupress.repository;

import com.edupress.domain.Assignment;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Assignment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long>, JpaSpecificationExecutor<Assignment> {}

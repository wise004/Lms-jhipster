package com.edupress.repository;

import com.edupress.domain.Enrollment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Enrollment entity.
 */
@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long>, JpaSpecificationExecutor<Enrollment> {
    default Optional<Enrollment> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Enrollment> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Enrollment> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select enrollment from Enrollment enrollment left join fetch enrollment.student",
        countQuery = "select count(enrollment) from Enrollment enrollment"
    )
    Page<Enrollment> findAllWithToOneRelationships(Pageable pageable);

    @Query("select enrollment from Enrollment enrollment left join fetch enrollment.student")
    List<Enrollment> findAllWithToOneRelationships();

    @Query("select enrollment from Enrollment enrollment left join fetch enrollment.student where enrollment.id =:id")
    Optional<Enrollment> findOneWithToOneRelationships(@Param("id") Long id);
}

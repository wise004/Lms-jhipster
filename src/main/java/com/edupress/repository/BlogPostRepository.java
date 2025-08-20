package com.edupress.repository;

import com.edupress.domain.BlogPost;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the BlogPost entity.
 */
@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long>, JpaSpecificationExecutor<BlogPost> {
    default Optional<BlogPost> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<BlogPost> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<BlogPost> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select blogPost from BlogPost blogPost left join fetch blogPost.author",
        countQuery = "select count(blogPost) from BlogPost blogPost"
    )
    Page<BlogPost> findAllWithToOneRelationships(Pageable pageable);

    @Query("select blogPost from BlogPost blogPost left join fetch blogPost.author")
    List<BlogPost> findAllWithToOneRelationships();

    @Query("select blogPost from BlogPost blogPost left join fetch blogPost.author where blogPost.id =:id")
    Optional<BlogPost> findOneWithToOneRelationships(@Param("id") Long id);
}

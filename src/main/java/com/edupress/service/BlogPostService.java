package com.edupress.service;

import com.edupress.domain.BlogPost;
import com.edupress.repository.BlogPostRepository;
import com.edupress.service.dto.BlogPostDTO;
import com.edupress.service.mapper.BlogPostMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.edupress.domain.BlogPost}.
 */
@Service
@Transactional
public class BlogPostService {

    private static final Logger LOG = LoggerFactory.getLogger(BlogPostService.class);

    private final BlogPostRepository blogPostRepository;

    private final BlogPostMapper blogPostMapper;

    public BlogPostService(BlogPostRepository blogPostRepository, BlogPostMapper blogPostMapper) {
        this.blogPostRepository = blogPostRepository;
        this.blogPostMapper = blogPostMapper;
    }

    /**
     * Save a blogPost.
     *
     * @param blogPostDTO the entity to save.
     * @return the persisted entity.
     */
    public BlogPostDTO save(BlogPostDTO blogPostDTO) {
        LOG.debug("Request to save BlogPost : {}", blogPostDTO);
        BlogPost blogPost = blogPostMapper.toEntity(blogPostDTO);
        blogPost = blogPostRepository.save(blogPost);
        return blogPostMapper.toDto(blogPost);
    }

    /**
     * Update a blogPost.
     *
     * @param blogPostDTO the entity to save.
     * @return the persisted entity.
     */
    public BlogPostDTO update(BlogPostDTO blogPostDTO) {
        LOG.debug("Request to update BlogPost : {}", blogPostDTO);
        BlogPost blogPost = blogPostMapper.toEntity(blogPostDTO);
        blogPost = blogPostRepository.save(blogPost);
        return blogPostMapper.toDto(blogPost);
    }

    /**
     * Partially update a blogPost.
     *
     * @param blogPostDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<BlogPostDTO> partialUpdate(BlogPostDTO blogPostDTO) {
        LOG.debug("Request to partially update BlogPost : {}", blogPostDTO);

        return blogPostRepository
            .findById(blogPostDTO.getId())
            .map(existingBlogPost -> {
                blogPostMapper.partialUpdate(existingBlogPost, blogPostDTO);

                return existingBlogPost;
            })
            .map(blogPostRepository::save)
            .map(blogPostMapper::toDto);
    }

    /**
     * Get all the blogPosts with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<BlogPostDTO> findAllWithEagerRelationships(Pageable pageable) {
        return blogPostRepository.findAllWithEagerRelationships(pageable).map(blogPostMapper::toDto);
    }

    /**
     * Get one blogPost by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BlogPostDTO> findOne(Long id) {
        LOG.debug("Request to get BlogPost : {}", id);
        return blogPostRepository.findOneWithEagerRelationships(id).map(blogPostMapper::toDto);
    }

    /**
     * Delete the blogPost by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete BlogPost : {}", id);
        blogPostRepository.deleteById(id);
    }
}

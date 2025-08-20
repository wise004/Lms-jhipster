package com.edupress.web.rest;

import static com.edupress.domain.BlogPostAsserts.*;
import static com.edupress.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.edupress.IntegrationTest;
import com.edupress.domain.AppUser;
import com.edupress.domain.BlogPost;
import com.edupress.domain.enumeration.PostStatus;
import com.edupress.repository.BlogPostRepository;
import com.edupress.service.BlogPostService;
import com.edupress.service.dto.BlogPostDTO;
import com.edupress.service.mapper.BlogPostMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link BlogPostResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class BlogPostResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_SLUG = "AAAAAAAAAA";
    private static final String UPDATED_SLUG = "BBBBBBBBBB";

    private static final String DEFAULT_SUMMARY = "AAAAAAAAAA";
    private static final String UPDATED_SUMMARY = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final String DEFAULT_COVER_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_COVER_IMAGE_URL = "BBBBBBBBBB";

    private static final Instant DEFAULT_PUBLISH_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PUBLISH_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final PostStatus DEFAULT_STATUS = PostStatus.DRAFT;
    private static final PostStatus UPDATED_STATUS = PostStatus.PUBLISHED;

    private static final String ENTITY_API_URL = "/api/blog-posts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BlogPostRepository blogPostRepository;

    @Mock
    private BlogPostRepository blogPostRepositoryMock;

    @Autowired
    private BlogPostMapper blogPostMapper;

    @Mock
    private BlogPostService blogPostServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBlogPostMockMvc;

    private BlogPost blogPost;

    private BlogPost insertedBlogPost;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BlogPost createEntity() {
        return new BlogPost()
            .title(DEFAULT_TITLE)
            .slug(DEFAULT_SLUG)
            .summary(DEFAULT_SUMMARY)
            .content(DEFAULT_CONTENT)
            .coverImageUrl(DEFAULT_COVER_IMAGE_URL)
            .publishDate(DEFAULT_PUBLISH_DATE)
            .status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BlogPost createUpdatedEntity() {
        return new BlogPost()
            .title(UPDATED_TITLE)
            .slug(UPDATED_SLUG)
            .summary(UPDATED_SUMMARY)
            .content(UPDATED_CONTENT)
            .coverImageUrl(UPDATED_COVER_IMAGE_URL)
            .publishDate(UPDATED_PUBLISH_DATE)
            .status(UPDATED_STATUS);
    }

    @BeforeEach
    void initTest() {
        blogPost = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedBlogPost != null) {
            blogPostRepository.delete(insertedBlogPost);
            insertedBlogPost = null;
        }
    }

    @Test
    @Transactional
    void createBlogPost() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the BlogPost
        BlogPostDTO blogPostDTO = blogPostMapper.toDto(blogPost);
        var returnedBlogPostDTO = om.readValue(
            restBlogPostMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(blogPostDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            BlogPostDTO.class
        );

        // Validate the BlogPost in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedBlogPost = blogPostMapper.toEntity(returnedBlogPostDTO);
        assertBlogPostUpdatableFieldsEquals(returnedBlogPost, getPersistedBlogPost(returnedBlogPost));

        insertedBlogPost = returnedBlogPost;
    }

    @Test
    @Transactional
    void createBlogPostWithExistingId() throws Exception {
        // Create the BlogPost with an existing ID
        blogPost.setId(1L);
        BlogPostDTO blogPostDTO = blogPostMapper.toDto(blogPost);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBlogPostMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(blogPostDTO)))
            .andExpect(status().isBadRequest());

        // Validate the BlogPost in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        blogPost.setTitle(null);

        // Create the BlogPost, which fails.
        BlogPostDTO blogPostDTO = blogPostMapper.toDto(blogPost);

        restBlogPostMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(blogPostDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSlugIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        blogPost.setSlug(null);

        // Create the BlogPost, which fails.
        BlogPostDTO blogPostDTO = blogPostMapper.toDto(blogPost);

        restBlogPostMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(blogPostDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        blogPost.setStatus(null);

        // Create the BlogPost, which fails.
        BlogPostDTO blogPostDTO = blogPostMapper.toDto(blogPost);

        restBlogPostMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(blogPostDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBlogPosts() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList
        restBlogPostMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(blogPost.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].slug").value(hasItem(DEFAULT_SLUG)))
            .andExpect(jsonPath("$.[*].summary").value(hasItem(DEFAULT_SUMMARY)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].coverImageUrl").value(hasItem(DEFAULT_COVER_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].publishDate").value(hasItem(DEFAULT_PUBLISH_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBlogPostsWithEagerRelationshipsIsEnabled() throws Exception {
        when(blogPostServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBlogPostMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(blogPostServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBlogPostsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(blogPostServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBlogPostMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(blogPostRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getBlogPost() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get the blogPost
        restBlogPostMockMvc
            .perform(get(ENTITY_API_URL_ID, blogPost.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(blogPost.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.slug").value(DEFAULT_SLUG))
            .andExpect(jsonPath("$.summary").value(DEFAULT_SUMMARY))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT))
            .andExpect(jsonPath("$.coverImageUrl").value(DEFAULT_COVER_IMAGE_URL))
            .andExpect(jsonPath("$.publishDate").value(DEFAULT_PUBLISH_DATE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getBlogPostsByIdFiltering() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        Long id = blogPost.getId();

        defaultBlogPostFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultBlogPostFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultBlogPostFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllBlogPostsByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where title equals to
        defaultBlogPostFiltering("title.equals=" + DEFAULT_TITLE, "title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllBlogPostsByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where title in
        defaultBlogPostFiltering("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE, "title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllBlogPostsByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where title is not null
        defaultBlogPostFiltering("title.specified=true", "title.specified=false");
    }

    @Test
    @Transactional
    void getAllBlogPostsByTitleContainsSomething() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where title contains
        defaultBlogPostFiltering("title.contains=" + DEFAULT_TITLE, "title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllBlogPostsByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where title does not contain
        defaultBlogPostFiltering("title.doesNotContain=" + UPDATED_TITLE, "title.doesNotContain=" + DEFAULT_TITLE);
    }

    @Test
    @Transactional
    void getAllBlogPostsBySlugIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where slug equals to
        defaultBlogPostFiltering("slug.equals=" + DEFAULT_SLUG, "slug.equals=" + UPDATED_SLUG);
    }

    @Test
    @Transactional
    void getAllBlogPostsBySlugIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where slug in
        defaultBlogPostFiltering("slug.in=" + DEFAULT_SLUG + "," + UPDATED_SLUG, "slug.in=" + UPDATED_SLUG);
    }

    @Test
    @Transactional
    void getAllBlogPostsBySlugIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where slug is not null
        defaultBlogPostFiltering("slug.specified=true", "slug.specified=false");
    }

    @Test
    @Transactional
    void getAllBlogPostsBySlugContainsSomething() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where slug contains
        defaultBlogPostFiltering("slug.contains=" + DEFAULT_SLUG, "slug.contains=" + UPDATED_SLUG);
    }

    @Test
    @Transactional
    void getAllBlogPostsBySlugNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where slug does not contain
        defaultBlogPostFiltering("slug.doesNotContain=" + UPDATED_SLUG, "slug.doesNotContain=" + DEFAULT_SLUG);
    }

    @Test
    @Transactional
    void getAllBlogPostsBySummaryIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where summary equals to
        defaultBlogPostFiltering("summary.equals=" + DEFAULT_SUMMARY, "summary.equals=" + UPDATED_SUMMARY);
    }

    @Test
    @Transactional
    void getAllBlogPostsBySummaryIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where summary in
        defaultBlogPostFiltering("summary.in=" + DEFAULT_SUMMARY + "," + UPDATED_SUMMARY, "summary.in=" + UPDATED_SUMMARY);
    }

    @Test
    @Transactional
    void getAllBlogPostsBySummaryIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where summary is not null
        defaultBlogPostFiltering("summary.specified=true", "summary.specified=false");
    }

    @Test
    @Transactional
    void getAllBlogPostsBySummaryContainsSomething() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where summary contains
        defaultBlogPostFiltering("summary.contains=" + DEFAULT_SUMMARY, "summary.contains=" + UPDATED_SUMMARY);
    }

    @Test
    @Transactional
    void getAllBlogPostsBySummaryNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where summary does not contain
        defaultBlogPostFiltering("summary.doesNotContain=" + UPDATED_SUMMARY, "summary.doesNotContain=" + DEFAULT_SUMMARY);
    }

    @Test
    @Transactional
    void getAllBlogPostsByCoverImageUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where coverImageUrl equals to
        defaultBlogPostFiltering("coverImageUrl.equals=" + DEFAULT_COVER_IMAGE_URL, "coverImageUrl.equals=" + UPDATED_COVER_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllBlogPostsByCoverImageUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where coverImageUrl in
        defaultBlogPostFiltering(
            "coverImageUrl.in=" + DEFAULT_COVER_IMAGE_URL + "," + UPDATED_COVER_IMAGE_URL,
            "coverImageUrl.in=" + UPDATED_COVER_IMAGE_URL
        );
    }

    @Test
    @Transactional
    void getAllBlogPostsByCoverImageUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where coverImageUrl is not null
        defaultBlogPostFiltering("coverImageUrl.specified=true", "coverImageUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllBlogPostsByCoverImageUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where coverImageUrl contains
        defaultBlogPostFiltering("coverImageUrl.contains=" + DEFAULT_COVER_IMAGE_URL, "coverImageUrl.contains=" + UPDATED_COVER_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllBlogPostsByCoverImageUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where coverImageUrl does not contain
        defaultBlogPostFiltering(
            "coverImageUrl.doesNotContain=" + UPDATED_COVER_IMAGE_URL,
            "coverImageUrl.doesNotContain=" + DEFAULT_COVER_IMAGE_URL
        );
    }

    @Test
    @Transactional
    void getAllBlogPostsByPublishDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where publishDate equals to
        defaultBlogPostFiltering("publishDate.equals=" + DEFAULT_PUBLISH_DATE, "publishDate.equals=" + UPDATED_PUBLISH_DATE);
    }

    @Test
    @Transactional
    void getAllBlogPostsByPublishDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where publishDate in
        defaultBlogPostFiltering(
            "publishDate.in=" + DEFAULT_PUBLISH_DATE + "," + UPDATED_PUBLISH_DATE,
            "publishDate.in=" + UPDATED_PUBLISH_DATE
        );
    }

    @Test
    @Transactional
    void getAllBlogPostsByPublishDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where publishDate is not null
        defaultBlogPostFiltering("publishDate.specified=true", "publishDate.specified=false");
    }

    @Test
    @Transactional
    void getAllBlogPostsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where status equals to
        defaultBlogPostFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllBlogPostsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where status in
        defaultBlogPostFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllBlogPostsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where status is not null
        defaultBlogPostFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllBlogPostsByAuthorIsEqualToSomething() throws Exception {
        AppUser author;
        if (TestUtil.findAll(em, AppUser.class).isEmpty()) {
            blogPostRepository.saveAndFlush(blogPost);
            author = AppUserResourceIT.createEntity();
        } else {
            author = TestUtil.findAll(em, AppUser.class).get(0);
        }
        em.persist(author);
        em.flush();
        blogPost.setAuthor(author);
        blogPostRepository.saveAndFlush(blogPost);
        Long authorId = author.getId();
        // Get all the blogPostList where author equals to authorId
        defaultBlogPostShouldBeFound("authorId.equals=" + authorId);

        // Get all the blogPostList where author equals to (authorId + 1)
        defaultBlogPostShouldNotBeFound("authorId.equals=" + (authorId + 1));
    }

    private void defaultBlogPostFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultBlogPostShouldBeFound(shouldBeFound);
        defaultBlogPostShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBlogPostShouldBeFound(String filter) throws Exception {
        restBlogPostMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(blogPost.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].slug").value(hasItem(DEFAULT_SLUG)))
            .andExpect(jsonPath("$.[*].summary").value(hasItem(DEFAULT_SUMMARY)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].coverImageUrl").value(hasItem(DEFAULT_COVER_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].publishDate").value(hasItem(DEFAULT_PUBLISH_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));

        // Check, that the count call also returns 1
        restBlogPostMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBlogPostShouldNotBeFound(String filter) throws Exception {
        restBlogPostMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBlogPostMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingBlogPost() throws Exception {
        // Get the blogPost
        restBlogPostMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBlogPost() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the blogPost
        BlogPost updatedBlogPost = blogPostRepository.findById(blogPost.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBlogPost are not directly saved in db
        em.detach(updatedBlogPost);
        updatedBlogPost
            .title(UPDATED_TITLE)
            .slug(UPDATED_SLUG)
            .summary(UPDATED_SUMMARY)
            .content(UPDATED_CONTENT)
            .coverImageUrl(UPDATED_COVER_IMAGE_URL)
            .publishDate(UPDATED_PUBLISH_DATE)
            .status(UPDATED_STATUS);
        BlogPostDTO blogPostDTO = blogPostMapper.toDto(updatedBlogPost);

        restBlogPostMockMvc
            .perform(
                put(ENTITY_API_URL_ID, blogPostDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(blogPostDTO))
            )
            .andExpect(status().isOk());

        // Validate the BlogPost in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBlogPostToMatchAllProperties(updatedBlogPost);
    }

    @Test
    @Transactional
    void putNonExistingBlogPost() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        blogPost.setId(longCount.incrementAndGet());

        // Create the BlogPost
        BlogPostDTO blogPostDTO = blogPostMapper.toDto(blogPost);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBlogPostMockMvc
            .perform(
                put(ENTITY_API_URL_ID, blogPostDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(blogPostDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BlogPost in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBlogPost() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        blogPost.setId(longCount.incrementAndGet());

        // Create the BlogPost
        BlogPostDTO blogPostDTO = blogPostMapper.toDto(blogPost);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBlogPostMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(blogPostDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BlogPost in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBlogPost() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        blogPost.setId(longCount.incrementAndGet());

        // Create the BlogPost
        BlogPostDTO blogPostDTO = blogPostMapper.toDto(blogPost);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBlogPostMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(blogPostDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BlogPost in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBlogPostWithPatch() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the blogPost using partial update
        BlogPost partialUpdatedBlogPost = new BlogPost();
        partialUpdatedBlogPost.setId(blogPost.getId());

        partialUpdatedBlogPost
            .title(UPDATED_TITLE)
            .slug(UPDATED_SLUG)
            .summary(UPDATED_SUMMARY)
            .content(UPDATED_CONTENT)
            .publishDate(UPDATED_PUBLISH_DATE);

        restBlogPostMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBlogPost.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBlogPost))
            )
            .andExpect(status().isOk());

        // Validate the BlogPost in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBlogPostUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedBlogPost, blogPost), getPersistedBlogPost(blogPost));
    }

    @Test
    @Transactional
    void fullUpdateBlogPostWithPatch() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the blogPost using partial update
        BlogPost partialUpdatedBlogPost = new BlogPost();
        partialUpdatedBlogPost.setId(blogPost.getId());

        partialUpdatedBlogPost
            .title(UPDATED_TITLE)
            .slug(UPDATED_SLUG)
            .summary(UPDATED_SUMMARY)
            .content(UPDATED_CONTENT)
            .coverImageUrl(UPDATED_COVER_IMAGE_URL)
            .publishDate(UPDATED_PUBLISH_DATE)
            .status(UPDATED_STATUS);

        restBlogPostMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBlogPost.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBlogPost))
            )
            .andExpect(status().isOk());

        // Validate the BlogPost in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBlogPostUpdatableFieldsEquals(partialUpdatedBlogPost, getPersistedBlogPost(partialUpdatedBlogPost));
    }

    @Test
    @Transactional
    void patchNonExistingBlogPost() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        blogPost.setId(longCount.incrementAndGet());

        // Create the BlogPost
        BlogPostDTO blogPostDTO = blogPostMapper.toDto(blogPost);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBlogPostMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, blogPostDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(blogPostDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BlogPost in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBlogPost() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        blogPost.setId(longCount.incrementAndGet());

        // Create the BlogPost
        BlogPostDTO blogPostDTO = blogPostMapper.toDto(blogPost);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBlogPostMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(blogPostDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BlogPost in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBlogPost() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        blogPost.setId(longCount.incrementAndGet());

        // Create the BlogPost
        BlogPostDTO blogPostDTO = blogPostMapper.toDto(blogPost);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBlogPostMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(blogPostDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BlogPost in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBlogPost() throws Exception {
        // Initialize the database
        insertedBlogPost = blogPostRepository.saveAndFlush(blogPost);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the blogPost
        restBlogPostMockMvc
            .perform(delete(ENTITY_API_URL_ID, blogPost.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return blogPostRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected BlogPost getPersistedBlogPost(BlogPost blogPost) {
        return blogPostRepository.findById(blogPost.getId()).orElseThrow();
    }

    protected void assertPersistedBlogPostToMatchAllProperties(BlogPost expectedBlogPost) {
        assertBlogPostAllPropertiesEquals(expectedBlogPost, getPersistedBlogPost(expectedBlogPost));
    }

    protected void assertPersistedBlogPostToMatchUpdatableProperties(BlogPost expectedBlogPost) {
        assertBlogPostAllUpdatablePropertiesEquals(expectedBlogPost, getPersistedBlogPost(expectedBlogPost));
    }
}

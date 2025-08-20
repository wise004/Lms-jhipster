package com.edupress.web.rest;

import com.edupress.repository.QuizAttemptRepository;
import com.edupress.service.QuizAttemptQueryService;
import com.edupress.service.QuizAttemptService;
import com.edupress.service.criteria.QuizAttemptCriteria;
import com.edupress.service.dto.QuizAttemptDTO;
import com.edupress.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.edupress.domain.QuizAttempt}.
 */
@RestController
@RequestMapping("/api/quiz-attempts")
public class QuizAttemptResource {

    private static final Logger LOG = LoggerFactory.getLogger(QuizAttemptResource.class);

    private static final String ENTITY_NAME = "quizAttempt";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final QuizAttemptService quizAttemptService;

    private final QuizAttemptRepository quizAttemptRepository;

    private final QuizAttemptQueryService quizAttemptQueryService;

    public QuizAttemptResource(
        QuizAttemptService quizAttemptService,
        QuizAttemptRepository quizAttemptRepository,
        QuizAttemptQueryService quizAttemptQueryService
    ) {
        this.quizAttemptService = quizAttemptService;
        this.quizAttemptRepository = quizAttemptRepository;
        this.quizAttemptQueryService = quizAttemptQueryService;
    }

    /**
     * {@code POST  /quiz-attempts} : Create a new quizAttempt.
     *
     * @param quizAttemptDTO the quizAttemptDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new quizAttemptDTO, or with status {@code 400 (Bad Request)} if the quizAttempt has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<QuizAttemptDTO> createQuizAttempt(@RequestBody QuizAttemptDTO quizAttemptDTO) throws URISyntaxException {
        LOG.debug("REST request to save QuizAttempt : {}", quizAttemptDTO);
        if (quizAttemptDTO.getId() != null) {
            throw new BadRequestAlertException("A new quizAttempt cannot already have an ID", ENTITY_NAME, "idexists");
        }
        quizAttemptDTO = quizAttemptService.save(quizAttemptDTO);
        return ResponseEntity.created(new URI("/api/quiz-attempts/" + quizAttemptDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, quizAttemptDTO.getId().toString()))
            .body(quizAttemptDTO);
    }

    /**
     * {@code PUT  /quiz-attempts/:id} : Updates an existing quizAttempt.
     *
     * @param id the id of the quizAttemptDTO to save.
     * @param quizAttemptDTO the quizAttemptDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated quizAttemptDTO,
     * or with status {@code 400 (Bad Request)} if the quizAttemptDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the quizAttemptDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<QuizAttemptDTO> updateQuizAttempt(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody QuizAttemptDTO quizAttemptDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update QuizAttempt : {}, {}", id, quizAttemptDTO);
        if (quizAttemptDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, quizAttemptDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!quizAttemptRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        quizAttemptDTO = quizAttemptService.update(quizAttemptDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, quizAttemptDTO.getId().toString()))
            .body(quizAttemptDTO);
    }

    /**
     * {@code PATCH  /quiz-attempts/:id} : Partial updates given fields of an existing quizAttempt, field will ignore if it is null
     *
     * @param id the id of the quizAttemptDTO to save.
     * @param quizAttemptDTO the quizAttemptDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated quizAttemptDTO,
     * or with status {@code 400 (Bad Request)} if the quizAttemptDTO is not valid,
     * or with status {@code 404 (Not Found)} if the quizAttemptDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the quizAttemptDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<QuizAttemptDTO> partialUpdateQuizAttempt(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody QuizAttemptDTO quizAttemptDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update QuizAttempt partially : {}, {}", id, quizAttemptDTO);
        if (quizAttemptDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, quizAttemptDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!quizAttemptRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<QuizAttemptDTO> result = quizAttemptService.partialUpdate(quizAttemptDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, quizAttemptDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /quiz-attempts} : get all the quizAttempts.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of quizAttempts in body.
     */
    @GetMapping("")
    public ResponseEntity<List<QuizAttemptDTO>> getAllQuizAttempts(
        QuizAttemptCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get QuizAttempts by criteria: {}", criteria);

        Page<QuizAttemptDTO> page = quizAttemptQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /quiz-attempts/count} : count all the quizAttempts.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countQuizAttempts(QuizAttemptCriteria criteria) {
        LOG.debug("REST request to count QuizAttempts by criteria: {}", criteria);
        return ResponseEntity.ok().body(quizAttemptQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /quiz-attempts/:id} : get the "id" quizAttempt.
     *
     * @param id the id of the quizAttemptDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the quizAttemptDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<QuizAttemptDTO> getQuizAttempt(@PathVariable("id") Long id) {
        LOG.debug("REST request to get QuizAttempt : {}", id);
        Optional<QuizAttemptDTO> quizAttemptDTO = quizAttemptService.findOne(id);
        return ResponseUtil.wrapOrNotFound(quizAttemptDTO);
    }

    /**
     * {@code DELETE  /quiz-attempts/:id} : delete the "id" quizAttempt.
     *
     * @param id the id of the quizAttemptDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuizAttempt(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete QuizAttempt : {}", id);
        quizAttemptService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}

package com.edupress.web.rest;

import com.edupress.repository.AppUserRepository;
import com.edupress.repository.EnrollmentRepository;
import com.edupress.security.SecurityUtils;
import com.edupress.service.EnrollmentQueryService;
import com.edupress.service.EnrollmentService;
import com.edupress.service.criteria.EnrollmentCriteria;
import com.edupress.service.dto.EnrollmentDTO;
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
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.edupress.domain.Enrollment}.
 */
@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentResource {

    private static final Logger LOG = LoggerFactory.getLogger(EnrollmentResource.class);

    private static final String ENTITY_NAME = "enrollment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EnrollmentService enrollmentService;

    private final EnrollmentRepository enrollmentRepository;

    private final EnrollmentQueryService enrollmentQueryService;
    private final AppUserRepository appUserRepository;

    public EnrollmentResource(
        EnrollmentService enrollmentService,
        EnrollmentRepository enrollmentRepository,
        EnrollmentQueryService enrollmentQueryService,
        AppUserRepository appUserRepository
    ) {
        this.enrollmentService = enrollmentService;
        this.enrollmentRepository = enrollmentRepository;
        this.enrollmentQueryService = enrollmentQueryService;
        this.appUserRepository = appUserRepository;
    }

    public static record ProgressUpdateRequest(String progress, Integer percent) {}

    /**
     * {@code POST  /enrollments} : Create a new enrollment.
     *
     * @param enrollmentDTO the enrollmentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new enrollmentDTO, or with status {@code 400 (Bad Request)} if the enrollment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<EnrollmentDTO> createEnrollment(@RequestBody EnrollmentDTO enrollmentDTO) throws URISyntaxException {
        LOG.debug("REST request to save Enrollment : {}", enrollmentDTO);
        if (enrollmentDTO.getId() != null) {
            throw new BadRequestAlertException("A new enrollment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        // attach current user as student if not provided
        if (enrollmentDTO.getStudent() == null || enrollmentDTO.getStudent().getId() == null) {
            var email = SecurityUtils.getCurrentUserLogin().orElse(null);
            if (email != null) {
                appUserRepository
                    .findOneByEmail(email)
                    .ifPresent(student -> {
                        var dto = new com.edupress.service.dto.AppUserDTO();
                        dto.setId(student.getId());
                        dto.setEmail(student.getEmail());
                        enrollmentDTO.setStudent(dto);
                    });
            }
        }
        var saved = enrollmentService.save(enrollmentDTO);
        return ResponseEntity.created(new URI("/api/enrollments/" + saved.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, saved.getId().toString()))
            .body(saved);
    }

    /**
     * {@code PUT  /enrollments/:id} : Updates an existing enrollment.
     *
     * @param id the id of the enrollmentDTO to save.
     * @param enrollmentDTO the enrollmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated enrollmentDTO,
     * or with status {@code 400 (Bad Request)} if the enrollmentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the enrollmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<EnrollmentDTO> updateEnrollment(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody EnrollmentDTO enrollmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Enrollment : {}, {}", id, enrollmentDTO);
        if (enrollmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, enrollmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!enrollmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        enrollmentDTO = enrollmentService.update(enrollmentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, enrollmentDTO.getId().toString()))
            .body(enrollmentDTO);
    }

    /**
     * {@code PATCH  /enrollments/:id} : Partial updates given fields of an existing enrollment, field will ignore if it is null
     *
     * @param id the id of the enrollmentDTO to save.
     * @param enrollmentDTO the enrollmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated enrollmentDTO,
     * or with status {@code 400 (Bad Request)} if the enrollmentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the enrollmentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the enrollmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<EnrollmentDTO> partialUpdateEnrollment(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody EnrollmentDTO enrollmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Enrollment partially : {}, {}", id, enrollmentDTO);
        if (enrollmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, enrollmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!enrollmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<EnrollmentDTO> result = enrollmentService.partialUpdate(enrollmentDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, enrollmentDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /enrollments} : get all the enrollments.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of enrollments in body.
     */
    @GetMapping("")
    public ResponseEntity<List<EnrollmentDTO>> getAllEnrollments(
        EnrollmentCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Enrollments by criteria: {}", criteria);

        Page<EnrollmentDTO> page = enrollmentQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /enrollments/mine} : get current user's enrollments, optionally filtered by courseId.
     * Ensures the studentId criteria is enforced to the authenticated user's AppUser id.
     */
    @GetMapping("/mine")
    public ResponseEntity<List<EnrollmentDTO>> getMyEnrollments(
        EnrollmentCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        var email = SecurityUtils.getCurrentUserLogin().orElse(null);
        if (email == null) {
            return ResponseEntity.status(401).build();
        }
        var studentIdOpt = appUserRepository.findOneByEmail(email).map(u -> u.getId());
        if (studentIdOpt.isEmpty()) {
            return ResponseEntity.ok().body(List.of());
        }
        long studentId = studentIdOpt.orElseThrow();
        // Force criteria to current user's studentId
        LongFilter studentIdFilter = new LongFilter();
        studentIdFilter.setEquals(studentId);
        criteria.setStudentId(studentIdFilter);

        Page<EnrollmentDTO> page = enrollmentQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /enrollments/count} : count all the enrollments.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countEnrollments(EnrollmentCriteria criteria) {
        LOG.debug("REST request to count Enrollments by criteria: {}", criteria);
        return ResponseEntity.ok().body(enrollmentQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /enrollments/:id} : get the "id" enrollment.
     *
     * @param id the id of the enrollmentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the enrollmentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentDTO> getEnrollment(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Enrollment : {}", id);
        Optional<EnrollmentDTO> enrollmentDTO = enrollmentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(enrollmentDTO);
    }

    /**
     * POST /enrollments/{id}/progress : update progress blob and percent; auto-complete at 100%.
     */
    @PostMapping("/{id}/progress")
    public ResponseEntity<EnrollmentDTO> updateProgress(@PathVariable("id") Long id, @RequestBody ProgressUpdateRequest body) {
        LOG.debug("REST request to update progress for Enrollment : {}", id);
        // ownership check: only owner or admin
        var email = SecurityUtils.getCurrentUserLogin().orElse(null);
        if (email != null) {
            var isOwner = enrollmentRepository
                .findById(id)
                .map(e -> e.getStudent() != null && email.equalsIgnoreCase(e.getStudent().getEmail()))
                .orElse(false);
            var isAdmin = SecurityUtils.hasCurrentUserThisAuthority("ROLE_ADMIN");
            if (!isOwner && !isAdmin) {
                return ResponseEntity.status(403).build();
            }
        }
        return ResponseUtil.wrapOrNotFound(enrollmentService.updateProgress(id, body.progress(), body.percent(), null));
    }

    /**
     * {@code DELETE  /enrollments/:id} : delete the "id" enrollment.
     *
     * @param id the id of the enrollmentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnrollment(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Enrollment : {}", id);
        enrollmentService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}

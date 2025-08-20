package com.edupress.service;

import com.edupress.domain.AssignmentSubmission;
import com.edupress.repository.AssignmentSubmissionRepository;
import com.edupress.service.dto.AssignmentSubmissionDTO;
import com.edupress.service.mapper.AssignmentSubmissionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.edupress.domain.AssignmentSubmission}.
 */
@Service
@Transactional
public class AssignmentSubmissionService {

    private static final Logger LOG = LoggerFactory.getLogger(AssignmentSubmissionService.class);

    private final AssignmentSubmissionRepository assignmentSubmissionRepository;

    private final AssignmentSubmissionMapper assignmentSubmissionMapper;

    public AssignmentSubmissionService(
        AssignmentSubmissionRepository assignmentSubmissionRepository,
        AssignmentSubmissionMapper assignmentSubmissionMapper
    ) {
        this.assignmentSubmissionRepository = assignmentSubmissionRepository;
        this.assignmentSubmissionMapper = assignmentSubmissionMapper;
    }

    /**
     * Save a assignmentSubmission.
     *
     * @param assignmentSubmissionDTO the entity to save.
     * @return the persisted entity.
     */
    public AssignmentSubmissionDTO save(AssignmentSubmissionDTO assignmentSubmissionDTO) {
        LOG.debug("Request to save AssignmentSubmission : {}", assignmentSubmissionDTO);
        AssignmentSubmission assignmentSubmission = assignmentSubmissionMapper.toEntity(assignmentSubmissionDTO);
        assignmentSubmission = assignmentSubmissionRepository.save(assignmentSubmission);
        return assignmentSubmissionMapper.toDto(assignmentSubmission);
    }

    /**
     * Update a assignmentSubmission.
     *
     * @param assignmentSubmissionDTO the entity to save.
     * @return the persisted entity.
     */
    public AssignmentSubmissionDTO update(AssignmentSubmissionDTO assignmentSubmissionDTO) {
        LOG.debug("Request to update AssignmentSubmission : {}", assignmentSubmissionDTO);
        AssignmentSubmission assignmentSubmission = assignmentSubmissionMapper.toEntity(assignmentSubmissionDTO);
        assignmentSubmission = assignmentSubmissionRepository.save(assignmentSubmission);
        return assignmentSubmissionMapper.toDto(assignmentSubmission);
    }

    /**
     * Partially update a assignmentSubmission.
     *
     * @param assignmentSubmissionDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<AssignmentSubmissionDTO> partialUpdate(AssignmentSubmissionDTO assignmentSubmissionDTO) {
        LOG.debug("Request to partially update AssignmentSubmission : {}", assignmentSubmissionDTO);

        return assignmentSubmissionRepository
            .findById(assignmentSubmissionDTO.getId())
            .map(existingAssignmentSubmission -> {
                assignmentSubmissionMapper.partialUpdate(existingAssignmentSubmission, assignmentSubmissionDTO);

                return existingAssignmentSubmission;
            })
            .map(assignmentSubmissionRepository::save)
            .map(assignmentSubmissionMapper::toDto);
    }

    /**
     * Get all the assignmentSubmissions with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<AssignmentSubmissionDTO> findAllWithEagerRelationships(Pageable pageable) {
        return assignmentSubmissionRepository.findAllWithEagerRelationships(pageable).map(assignmentSubmissionMapper::toDto);
    }

    /**
     * Get one assignmentSubmission by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AssignmentSubmissionDTO> findOne(Long id) {
        LOG.debug("Request to get AssignmentSubmission : {}", id);
        return assignmentSubmissionRepository.findOneWithEagerRelationships(id).map(assignmentSubmissionMapper::toDto);
    }

    /**
     * Delete the assignmentSubmission by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete AssignmentSubmission : {}", id);
        assignmentSubmissionRepository.deleteById(id);
    }
}

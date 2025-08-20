package com.edupress.service;

import com.edupress.domain.Assignment;
import com.edupress.repository.AssignmentRepository;
import com.edupress.service.dto.AssignmentDTO;
import com.edupress.service.mapper.AssignmentMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.edupress.domain.Assignment}.
 */
@Service
@Transactional
public class AssignmentService {

    private static final Logger LOG = LoggerFactory.getLogger(AssignmentService.class);

    private final AssignmentRepository assignmentRepository;

    private final AssignmentMapper assignmentMapper;

    public AssignmentService(AssignmentRepository assignmentRepository, AssignmentMapper assignmentMapper) {
        this.assignmentRepository = assignmentRepository;
        this.assignmentMapper = assignmentMapper;
    }

    /**
     * Save a assignment.
     *
     * @param assignmentDTO the entity to save.
     * @return the persisted entity.
     */
    public AssignmentDTO save(AssignmentDTO assignmentDTO) {
        LOG.debug("Request to save Assignment : {}", assignmentDTO);
        Assignment assignment = assignmentMapper.toEntity(assignmentDTO);
        assignment = assignmentRepository.save(assignment);
        return assignmentMapper.toDto(assignment);
    }

    /**
     * Update a assignment.
     *
     * @param assignmentDTO the entity to save.
     * @return the persisted entity.
     */
    public AssignmentDTO update(AssignmentDTO assignmentDTO) {
        LOG.debug("Request to update Assignment : {}", assignmentDTO);
        Assignment assignment = assignmentMapper.toEntity(assignmentDTO);
        assignment = assignmentRepository.save(assignment);
        return assignmentMapper.toDto(assignment);
    }

    /**
     * Partially update a assignment.
     *
     * @param assignmentDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<AssignmentDTO> partialUpdate(AssignmentDTO assignmentDTO) {
        LOG.debug("Request to partially update Assignment : {}", assignmentDTO);

        return assignmentRepository
            .findById(assignmentDTO.getId())
            .map(existingAssignment -> {
                assignmentMapper.partialUpdate(existingAssignment, assignmentDTO);

                return existingAssignment;
            })
            .map(assignmentRepository::save)
            .map(assignmentMapper::toDto);
    }

    /**
     * Get one assignment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AssignmentDTO> findOne(Long id) {
        LOG.debug("Request to get Assignment : {}", id);
        return assignmentRepository.findById(id).map(assignmentMapper::toDto);
    }

    /**
     * Delete the assignment by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Assignment : {}", id);
        assignmentRepository.deleteById(id);
    }
}

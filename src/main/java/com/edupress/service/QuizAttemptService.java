package com.edupress.service;

import com.edupress.domain.QuizAttempt;
import com.edupress.repository.QuizAttemptRepository;
import com.edupress.service.dto.QuizAttemptDTO;
import com.edupress.service.mapper.QuizAttemptMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.edupress.domain.QuizAttempt}.
 */
@Service
@Transactional
public class QuizAttemptService {

    private static final Logger LOG = LoggerFactory.getLogger(QuizAttemptService.class);

    private final QuizAttemptRepository quizAttemptRepository;

    private final QuizAttemptMapper quizAttemptMapper;

    public QuizAttemptService(QuizAttemptRepository quizAttemptRepository, QuizAttemptMapper quizAttemptMapper) {
        this.quizAttemptRepository = quizAttemptRepository;
        this.quizAttemptMapper = quizAttemptMapper;
    }

    /**
     * Save a quizAttempt.
     *
     * @param quizAttemptDTO the entity to save.
     * @return the persisted entity.
     */
    public QuizAttemptDTO save(QuizAttemptDTO quizAttemptDTO) {
        LOG.debug("Request to save QuizAttempt : {}", quizAttemptDTO);
        QuizAttempt quizAttempt = quizAttemptMapper.toEntity(quizAttemptDTO);
        quizAttempt = quizAttemptRepository.save(quizAttempt);
        return quizAttemptMapper.toDto(quizAttempt);
    }

    /**
     * Update a quizAttempt.
     *
     * @param quizAttemptDTO the entity to save.
     * @return the persisted entity.
     */
    public QuizAttemptDTO update(QuizAttemptDTO quizAttemptDTO) {
        LOG.debug("Request to update QuizAttempt : {}", quizAttemptDTO);
        QuizAttempt quizAttempt = quizAttemptMapper.toEntity(quizAttemptDTO);
        quizAttempt = quizAttemptRepository.save(quizAttempt);
        return quizAttemptMapper.toDto(quizAttempt);
    }

    /**
     * Partially update a quizAttempt.
     *
     * @param quizAttemptDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<QuizAttemptDTO> partialUpdate(QuizAttemptDTO quizAttemptDTO) {
        LOG.debug("Request to partially update QuizAttempt : {}", quizAttemptDTO);

        return quizAttemptRepository
            .findById(quizAttemptDTO.getId())
            .map(existingQuizAttempt -> {
                quizAttemptMapper.partialUpdate(existingQuizAttempt, quizAttemptDTO);

                return existingQuizAttempt;
            })
            .map(quizAttemptRepository::save)
            .map(quizAttemptMapper::toDto);
    }

    /**
     * Get all the quizAttempts with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<QuizAttemptDTO> findAllWithEagerRelationships(Pageable pageable) {
        return quizAttemptRepository.findAllWithEagerRelationships(pageable).map(quizAttemptMapper::toDto);
    }

    /**
     * Get one quizAttempt by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<QuizAttemptDTO> findOne(Long id) {
        LOG.debug("Request to get QuizAttempt : {}", id);
        return quizAttemptRepository.findOneWithEagerRelationships(id).map(quizAttemptMapper::toDto);
    }

    /**
     * Delete the quizAttempt by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete QuizAttempt : {}", id);
        quizAttemptRepository.deleteById(id);
    }
}

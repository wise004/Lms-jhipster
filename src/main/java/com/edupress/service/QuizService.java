package com.edupress.service;

import com.edupress.domain.Quiz;
import com.edupress.repository.QuizRepository;
import com.edupress.service.dto.QuizDTO;
import com.edupress.service.mapper.QuizMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.edupress.domain.Quiz}.
 */
@Service
@Transactional
public class QuizService {

    private static final Logger LOG = LoggerFactory.getLogger(QuizService.class);

    private final QuizRepository quizRepository;

    private final QuizMapper quizMapper;

    public QuizService(QuizRepository quizRepository, QuizMapper quizMapper) {
        this.quizRepository = quizRepository;
        this.quizMapper = quizMapper;
    }

    /**
     * Save a quiz.
     *
     * @param quizDTO the entity to save.
     * @return the persisted entity.
     */
    public QuizDTO save(QuizDTO quizDTO) {
        LOG.debug("Request to save Quiz : {}", quizDTO);
        Quiz quiz = quizMapper.toEntity(quizDTO);
        quiz = quizRepository.save(quiz);
        return quizMapper.toDto(quiz);
    }

    /**
     * Update a quiz.
     *
     * @param quizDTO the entity to save.
     * @return the persisted entity.
     */
    public QuizDTO update(QuizDTO quizDTO) {
        LOG.debug("Request to update Quiz : {}", quizDTO);
        Quiz quiz = quizMapper.toEntity(quizDTO);
        quiz = quizRepository.save(quiz);
        return quizMapper.toDto(quiz);
    }

    /**
     * Partially update a quiz.
     *
     * @param quizDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<QuizDTO> partialUpdate(QuizDTO quizDTO) {
        LOG.debug("Request to partially update Quiz : {}", quizDTO);

        return quizRepository
            .findById(quizDTO.getId())
            .map(existingQuiz -> {
                quizMapper.partialUpdate(existingQuiz, quizDTO);

                return existingQuiz;
            })
            .map(quizRepository::save)
            .map(quizMapper::toDto);
    }

    /**
     * Get one quiz by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<QuizDTO> findOne(Long id) {
        LOG.debug("Request to get Quiz : {}", id);
        return quizRepository.findById(id).map(quizMapper::toDto);
    }

    /**
     * Delete the quiz by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Quiz : {}", id);
        quizRepository.deleteById(id);
    }
}

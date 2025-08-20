package com.edupress.service;

import com.edupress.domain.Lesson;
import com.edupress.repository.LessonRepository;
import com.edupress.service.dto.LessonDTO;
import com.edupress.service.mapper.LessonMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.edupress.domain.Lesson}.
 */
@Service
@Transactional
public class LessonService {

    private static final Logger LOG = LoggerFactory.getLogger(LessonService.class);

    private final LessonRepository lessonRepository;

    private final LessonMapper lessonMapper;

    public LessonService(LessonRepository lessonRepository, LessonMapper lessonMapper) {
        this.lessonRepository = lessonRepository;
        this.lessonMapper = lessonMapper;
    }

    /**
     * Save a lesson.
     *
     * @param lessonDTO the entity to save.
     * @return the persisted entity.
     */
    public LessonDTO save(LessonDTO lessonDTO) {
        LOG.debug("Request to save Lesson : {}", lessonDTO);
        Lesson lesson = lessonMapper.toEntity(lessonDTO);
        lesson = lessonRepository.save(lesson);
        return lessonMapper.toDto(lesson);
    }

    /**
     * Update a lesson.
     *
     * @param lessonDTO the entity to save.
     * @return the persisted entity.
     */
    public LessonDTO update(LessonDTO lessonDTO) {
        LOG.debug("Request to update Lesson : {}", lessonDTO);
        Lesson lesson = lessonMapper.toEntity(lessonDTO);
        lesson = lessonRepository.save(lesson);
        return lessonMapper.toDto(lesson);
    }

    /**
     * Partially update a lesson.
     *
     * @param lessonDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<LessonDTO> partialUpdate(LessonDTO lessonDTO) {
        LOG.debug("Request to partially update Lesson : {}", lessonDTO);

        return lessonRepository
            .findById(lessonDTO.getId())
            .map(existingLesson -> {
                lessonMapper.partialUpdate(existingLesson, lessonDTO);

                return existingLesson;
            })
            .map(lessonRepository::save)
            .map(lessonMapper::toDto);
    }

    /**
     * Get one lesson by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<LessonDTO> findOne(Long id) {
        LOG.debug("Request to get Lesson : {}", id);
        return lessonRepository.findById(id).map(lessonMapper::toDto);
    }

    /**
     * Delete the lesson by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Lesson : {}", id);
        lessonRepository.deleteById(id);
    }
}

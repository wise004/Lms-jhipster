package com.edupress.service;

import com.edupress.domain.Certificate;
import com.edupress.repository.CertificateRepository;
import com.edupress.service.dto.CertificateDTO;
import com.edupress.service.mapper.CertificateMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.edupress.domain.Certificate}.
 */
@Service
@Transactional
public class CertificateService {

    private static final Logger LOG = LoggerFactory.getLogger(CertificateService.class);

    private final CertificateRepository certificateRepository;

    private final CertificateMapper certificateMapper;

    public CertificateService(CertificateRepository certificateRepository, CertificateMapper certificateMapper) {
        this.certificateRepository = certificateRepository;
        this.certificateMapper = certificateMapper;
    }

    /**
     * Save a certificate.
     *
     * @param certificateDTO the entity to save.
     * @return the persisted entity.
     */
    public CertificateDTO save(CertificateDTO certificateDTO) {
        LOG.debug("Request to save Certificate : {}", certificateDTO);
        Certificate certificate = certificateMapper.toEntity(certificateDTO);
        certificate = certificateRepository.save(certificate);
        return certificateMapper.toDto(certificate);
    }

    /**
     * Update a certificate.
     *
     * @param certificateDTO the entity to save.
     * @return the persisted entity.
     */
    public CertificateDTO update(CertificateDTO certificateDTO) {
        LOG.debug("Request to update Certificate : {}", certificateDTO);
        Certificate certificate = certificateMapper.toEntity(certificateDTO);
        certificate = certificateRepository.save(certificate);
        return certificateMapper.toDto(certificate);
    }

    /**
     * Partially update a certificate.
     *
     * @param certificateDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CertificateDTO> partialUpdate(CertificateDTO certificateDTO) {
        LOG.debug("Request to partially update Certificate : {}", certificateDTO);

        return certificateRepository
            .findById(certificateDTO.getId())
            .map(existingCertificate -> {
                certificateMapper.partialUpdate(existingCertificate, certificateDTO);

                return existingCertificate;
            })
            .map(certificateRepository::save)
            .map(certificateMapper::toDto);
    }

    /**
     * Get one certificate by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CertificateDTO> findOne(Long id) {
        LOG.debug("Request to get Certificate : {}", id);
        return certificateRepository.findById(id).map(certificateMapper::toDto);
    }

    /**
     * Delete the certificate by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Certificate : {}", id);
        certificateRepository.deleteById(id);
    }
}

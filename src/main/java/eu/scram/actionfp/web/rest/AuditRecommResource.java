package eu.scram.actionfp.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import eu.scram.actionfp.domain.AuditRecomm;
import eu.scram.actionfp.repository.AuditRecommRepository;
import eu.scram.actionfp.repository.search.AuditRecommSearchRepository;
import eu.scram.actionfp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link eu.scram.actionfp.domain.AuditRecomm}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class AuditRecommResource {

    private final Logger log = LoggerFactory.getLogger(AuditRecommResource.class);

    private static final String ENTITY_NAME = "auditRecomm";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AuditRecommRepository auditRecommRepository;

    private final AuditRecommSearchRepository auditRecommSearchRepository;

    public AuditRecommResource(AuditRecommRepository auditRecommRepository, AuditRecommSearchRepository auditRecommSearchRepository) {
        this.auditRecommRepository = auditRecommRepository;
        this.auditRecommSearchRepository = auditRecommSearchRepository;
    }

    /**
     * {@code POST  /audit-recomms} : Create a new auditRecomm.
     *
     * @param auditRecomm the auditRecomm to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new auditRecomm, or with status {@code 400 (Bad Request)} if the auditRecomm has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/audit-recomms")
    public ResponseEntity<AuditRecomm> createAuditRecomm(@RequestBody AuditRecomm auditRecomm) throws URISyntaxException {
        log.debug("REST request to save AuditRecomm : {}", auditRecomm);
        if (auditRecomm.getId() != null) {
            throw new BadRequestAlertException("A new auditRecomm cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AuditRecomm result = auditRecommRepository.save(auditRecomm);
        auditRecommSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/audit-recomms/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /audit-recomms/:id} : Updates an existing auditRecomm.
     *
     * @param id the id of the auditRecomm to save.
     * @param auditRecomm the auditRecomm to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated auditRecomm,
     * or with status {@code 400 (Bad Request)} if the auditRecomm is not valid,
     * or with status {@code 500 (Internal Server Error)} if the auditRecomm couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/audit-recomms/{id}")
    public ResponseEntity<AuditRecomm> updateAuditRecomm(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody AuditRecomm auditRecomm
    ) throws URISyntaxException {
        log.debug("REST request to update AuditRecomm : {}, {}", id, auditRecomm);
        if (auditRecomm.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, auditRecomm.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!auditRecommRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        AuditRecomm result = auditRecommRepository.save(auditRecomm);
        auditRecommSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, auditRecomm.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /audit-recomms/:id} : Partial updates given fields of an existing auditRecomm, field will ignore if it is null
     *
     * @param id the id of the auditRecomm to save.
     * @param auditRecomm the auditRecomm to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated auditRecomm,
     * or with status {@code 400 (Bad Request)} if the auditRecomm is not valid,
     * or with status {@code 404 (Not Found)} if the auditRecomm is not found,
     * or with status {@code 500 (Internal Server Error)} if the auditRecomm couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/audit-recomms/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<AuditRecomm> partialUpdateAuditRecomm(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody AuditRecomm auditRecomm
    ) throws URISyntaxException {
        log.debug("REST request to partial update AuditRecomm partially : {}, {}", id, auditRecomm);
        if (auditRecomm.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, auditRecomm.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!auditRecommRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AuditRecomm> result = auditRecommRepository
            .findById(auditRecomm.getId())
            .map(
                existingAuditRecomm -> {
                    if (auditRecomm.getRecommNumber() != null) {
                        existingAuditRecomm.setRecommNumber(auditRecomm.getRecommNumber());
                    }
                    if (auditRecomm.getPriority() != null) {
                        existingAuditRecomm.setPriority(auditRecomm.getPriority());
                    }
                    if (auditRecomm.getDescription() != null) {
                        existingAuditRecomm.setDescription(auditRecomm.getDescription());
                    }

                    return existingAuditRecomm;
                }
            )
            .map(auditRecommRepository::save)
            .map(
                savedAuditRecomm -> {
                    auditRecommSearchRepository.save(savedAuditRecomm);

                    return savedAuditRecomm;
                }
            );

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, auditRecomm.getId().toString())
        );
    }

    /**
     * {@code GET  /audit-recomms} : get all the auditRecomms.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of auditRecomms in body.
     */
    @GetMapping("/audit-recomms")
    public List<AuditRecomm> getAllAuditRecomms() {
        log.debug("REST request to get all AuditRecomms");
        return auditRecommRepository.findAll();
    }

    /**
     * {@code GET  /audit-recomms/:id} : get the "id" auditRecomm.
     *
     * @param id the id of the auditRecomm to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the auditRecomm, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/audit-recomms/{id}")
    public ResponseEntity<AuditRecomm> getAuditRecomm(@PathVariable Long id) {
        log.debug("REST request to get AuditRecomm : {}", id);
        Optional<AuditRecomm> auditRecomm = auditRecommRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(auditRecomm);
    }

    /**
     * {@code DELETE  /audit-recomms/:id} : delete the "id" auditRecomm.
     *
     * @param id the id of the auditRecomm to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/audit-recomms/{id}")
    public ResponseEntity<Void> deleteAuditRecomm(@PathVariable Long id) {
        log.debug("REST request to delete AuditRecomm : {}", id);
        auditRecommRepository.deleteById(id);
        auditRecommSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/audit-recomms?query=:query} : search for the auditRecomm corresponding
     * to the query.
     *
     * @param query the query of the auditRecomm search.
     * @return the result of the search.
     */
    @GetMapping("/_search/audit-recomms")
    public List<AuditRecomm> searchAuditRecomms(@RequestParam String query) {
        log.debug("REST request to search AuditRecomms for query {}", query);
        return StreamSupport
            .stream(auditRecommSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}

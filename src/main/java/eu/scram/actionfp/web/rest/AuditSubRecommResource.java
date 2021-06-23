package eu.scram.actionfp.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import eu.scram.actionfp.domain.AuditSubRecomm;
import eu.scram.actionfp.repository.AuditSubRecommRepository;
import eu.scram.actionfp.repository.search.AuditSubRecommSearchRepository;
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
 * REST controller for managing {@link eu.scram.actionfp.domain.AuditSubRecomm}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class AuditSubRecommResource {

    private final Logger log = LoggerFactory.getLogger(AuditSubRecommResource.class);

    private static final String ENTITY_NAME = "auditSubRecomm";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AuditSubRecommRepository auditSubRecommRepository;

    private final AuditSubRecommSearchRepository auditSubRecommSearchRepository;

    public AuditSubRecommResource(
        AuditSubRecommRepository auditSubRecommRepository,
        AuditSubRecommSearchRepository auditSubRecommSearchRepository
    ) {
        this.auditSubRecommRepository = auditSubRecommRepository;
        this.auditSubRecommSearchRepository = auditSubRecommSearchRepository;
    }

    /**
     * {@code POST  /audit-sub-recomms} : Create a new auditSubRecomm.
     *
     * @param auditSubRecomm the auditSubRecomm to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new auditSubRecomm, or with status {@code 400 (Bad Request)} if the auditSubRecomm has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/audit-sub-recomms")
    public ResponseEntity<AuditSubRecomm> createAuditSubRecomm(@RequestBody AuditSubRecomm auditSubRecomm) throws URISyntaxException {
        log.debug("REST request to save AuditSubRecomm : {}", auditSubRecomm);
        if (auditSubRecomm.getId() != null) {
            throw new BadRequestAlertException("A new auditSubRecomm cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AuditSubRecomm result = auditSubRecommRepository.save(auditSubRecomm);
        auditSubRecommSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/audit-sub-recomms/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /audit-sub-recomms/:id} : Updates an existing auditSubRecomm.
     *
     * @param id the id of the auditSubRecomm to save.
     * @param auditSubRecomm the auditSubRecomm to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated auditSubRecomm,
     * or with status {@code 400 (Bad Request)} if the auditSubRecomm is not valid,
     * or with status {@code 500 (Internal Server Error)} if the auditSubRecomm couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/audit-sub-recomms/{id}")
    public ResponseEntity<AuditSubRecomm> updateAuditSubRecomm(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody AuditSubRecomm auditSubRecomm
    ) throws URISyntaxException {
        log.debug("REST request to update AuditSubRecomm : {}, {}", id, auditSubRecomm);
        if (auditSubRecomm.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, auditSubRecomm.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!auditSubRecommRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        AuditSubRecomm result = auditSubRecommRepository.save(auditSubRecomm);
        auditSubRecommSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, auditSubRecomm.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /audit-sub-recomms/:id} : Partial updates given fields of an existing auditSubRecomm, field will ignore if it is null
     *
     * @param id the id of the auditSubRecomm to save.
     * @param auditSubRecomm the auditSubRecomm to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated auditSubRecomm,
     * or with status {@code 400 (Bad Request)} if the auditSubRecomm is not valid,
     * or with status {@code 404 (Not Found)} if the auditSubRecomm is not found,
     * or with status {@code 500 (Internal Server Error)} if the auditSubRecomm couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/audit-sub-recomms/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<AuditSubRecomm> partialUpdateAuditSubRecomm(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody AuditSubRecomm auditSubRecomm
    ) throws URISyntaxException {
        log.debug("REST request to partial update AuditSubRecomm partially : {}, {}", id, auditSubRecomm);
        if (auditSubRecomm.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, auditSubRecomm.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!auditSubRecommRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AuditSubRecomm> result = auditSubRecommRepository
            .findById(auditSubRecomm.getId())
            .map(
                existingAuditSubRecomm -> {
                    if (auditSubRecomm.getSubRecommNum() != null) {
                        existingAuditSubRecomm.setSubRecommNum(auditSubRecomm.getSubRecommNum());
                    }
                    if (auditSubRecomm.getDescription() != null) {
                        existingAuditSubRecomm.setDescription(auditSubRecomm.getDescription());
                    }

                    return existingAuditSubRecomm;
                }
            )
            .map(auditSubRecommRepository::save)
            .map(
                savedAuditSubRecomm -> {
                    auditSubRecommSearchRepository.save(savedAuditSubRecomm);

                    return savedAuditSubRecomm;
                }
            );

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, auditSubRecomm.getId().toString())
        );
    }

    /**
     * {@code GET  /audit-sub-recomms} : get all the auditSubRecomms.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of auditSubRecomms in body.
     */
    @GetMapping("/audit-sub-recomms")
    public List<AuditSubRecomm> getAllAuditSubRecomms() {
        log.debug("REST request to get all AuditSubRecomms");
        return auditSubRecommRepository.findAll();
    }

    /**
     * {@code GET  /audit-sub-recomms/:id} : get the "id" auditSubRecomm.
     *
     * @param id the id of the auditSubRecomm to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the auditSubRecomm, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/audit-sub-recomms/{id}")
    public ResponseEntity<AuditSubRecomm> getAuditSubRecomm(@PathVariable Long id) {
        log.debug("REST request to get AuditSubRecomm : {}", id);
        Optional<AuditSubRecomm> auditSubRecomm = auditSubRecommRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(auditSubRecomm);
    }

    /**
     * {@code DELETE  /audit-sub-recomms/:id} : delete the "id" auditSubRecomm.
     *
     * @param id the id of the auditSubRecomm to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/audit-sub-recomms/{id}")
    public ResponseEntity<Void> deleteAuditSubRecomm(@PathVariable Long id) {
        log.debug("REST request to delete AuditSubRecomm : {}", id);
        auditSubRecommRepository.deleteById(id);
        auditSubRecommSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/audit-sub-recomms?query=:query} : search for the auditSubRecomm corresponding
     * to the query.
     *
     * @param query the query of the auditSubRecomm search.
     * @return the result of the search.
     */
    @GetMapping("/_search/audit-sub-recomms")
    public List<AuditSubRecomm> searchAuditSubRecomms(@RequestParam String query) {
        log.debug("REST request to search AuditSubRecomms for query {}", query);
        return StreamSupport
            .stream(auditSubRecommSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}

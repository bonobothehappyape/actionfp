package eu.scram.actionfp.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import eu.scram.actionfp.domain.AuditReport;
import eu.scram.actionfp.repository.AuditReportRepository;
import eu.scram.actionfp.repository.search.AuditReportSearchRepository;
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
 * REST controller for managing {@link eu.scram.actionfp.domain.AuditReport}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class AuditReportResource {

    private final Logger log = LoggerFactory.getLogger(AuditReportResource.class);

    private static final String ENTITY_NAME = "auditReport";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AuditReportRepository auditReportRepository;

    private final AuditReportSearchRepository auditReportSearchRepository;

    public AuditReportResource(AuditReportRepository auditReportRepository, AuditReportSearchRepository auditReportSearchRepository) {
        this.auditReportRepository = auditReportRepository;
        this.auditReportSearchRepository = auditReportSearchRepository;
    }

    /**
     * {@code POST  /audit-reports} : Create a new auditReport.
     *
     * @param auditReport the auditReport to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new auditReport, or with status {@code 400 (Bad Request)} if the auditReport has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/audit-reports")
    public ResponseEntity<AuditReport> createAuditReport(@RequestBody AuditReport auditReport) throws URISyntaxException {
        log.debug("REST request to save AuditReport : {}", auditReport);
        if (auditReport.getId() != null) {
            throw new BadRequestAlertException("A new auditReport cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AuditReport result = auditReportRepository.save(auditReport);
        auditReportSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/audit-reports/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /audit-reports/:id} : Updates an existing auditReport.
     *
     * @param id the id of the auditReport to save.
     * @param auditReport the auditReport to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated auditReport,
     * or with status {@code 400 (Bad Request)} if the auditReport is not valid,
     * or with status {@code 500 (Internal Server Error)} if the auditReport couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/audit-reports/{id}")
    public ResponseEntity<AuditReport> updateAuditReport(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody AuditReport auditReport
    ) throws URISyntaxException {
        log.debug("REST request to update AuditReport : {}, {}", id, auditReport);
        if (auditReport.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, auditReport.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!auditReportRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        AuditReport result = auditReportRepository.save(auditReport);
        auditReportSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, auditReport.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /audit-reports/:id} : Partial updates given fields of an existing auditReport, field will ignore if it is null
     *
     * @param id the id of the auditReport to save.
     * @param auditReport the auditReport to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated auditReport,
     * or with status {@code 400 (Bad Request)} if the auditReport is not valid,
     * or with status {@code 404 (Not Found)} if the auditReport is not found,
     * or with status {@code 500 (Internal Server Error)} if the auditReport couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/audit-reports/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<AuditReport> partialUpdateAuditReport(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody AuditReport auditReport
    ) throws URISyntaxException {
        log.debug("REST request to partial update AuditReport partially : {}, {}", id, auditReport);
        if (auditReport.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, auditReport.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!auditReportRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AuditReport> result = auditReportRepository
            .findById(auditReport.getId())
            .map(
                existingAuditReport -> {
                    if (auditReport.getYear() != null) {
                        existingAuditReport.setYear(auditReport.getYear());
                    }
                    if (auditReport.getReportTitle() != null) {
                        existingAuditReport.setReportTitle(auditReport.getReportTitle());
                    }
                    if (auditReport.getInstitution() != null) {
                        existingAuditReport.setInstitution(auditReport.getInstitution());
                    }
                    if (auditReport.getReportDescription() != null) {
                        existingAuditReport.setReportDescription(auditReport.getReportDescription());
                    }

                    return existingAuditReport;
                }
            )
            .map(auditReportRepository::save)
            .map(
                savedAuditReport -> {
                    auditReportSearchRepository.save(savedAuditReport);

                    return savedAuditReport;
                }
            );

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, auditReport.getId().toString())
        );
    }

    /**
     * {@code GET  /audit-reports} : get all the auditReports.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of auditReports in body.
     */
    @GetMapping("/audit-reports")
    public List<AuditReport> getAllAuditReports() {
        log.debug("REST request to get all AuditReports");
        return auditReportRepository.findAll();
    }

    /**
     * {@code GET  /audit-reports/:id} : get the "id" auditReport.
     *
     * @param id the id of the auditReport to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the auditReport, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/audit-reports/{id}")
    public ResponseEntity<AuditReport> getAuditReport(@PathVariable Long id) {
        log.debug("REST request to get AuditReport : {}", id);
        Optional<AuditReport> auditReport = auditReportRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(auditReport);
    }

    /**
     * {@code DELETE  /audit-reports/:id} : delete the "id" auditReport.
     *
     * @param id the id of the auditReport to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/audit-reports/{id}")
    public ResponseEntity<Void> deleteAuditReport(@PathVariable Long id) {
        log.debug("REST request to delete AuditReport : {}", id);
        auditReportRepository.deleteById(id);
        auditReportSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/audit-reports?query=:query} : search for the auditReport corresponding
     * to the query.
     *
     * @param query the query of the auditReport search.
     * @return the result of the search.
     */
    @GetMapping("/_search/audit-reports")
    public List<AuditReport> searchAuditReports(@RequestParam String query) {
        log.debug("REST request to search AuditReports for query {}", query);
        return StreamSupport
            .stream(auditReportSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}

package eu.scram.actionfp.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import eu.scram.actionfp.domain.ICSRecomm;
import eu.scram.actionfp.repository.ICSRecommRepository;
import eu.scram.actionfp.repository.search.ICSRecommSearchRepository;
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
 * REST controller for managing {@link eu.scram.actionfp.domain.ICSRecomm}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ICSRecommResource {

    private final Logger log = LoggerFactory.getLogger(ICSRecommResource.class);

    private static final String ENTITY_NAME = "iCSRecomm";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ICSRecommRepository iCSRecommRepository;

    private final ICSRecommSearchRepository iCSRecommSearchRepository;

    public ICSRecommResource(ICSRecommRepository iCSRecommRepository, ICSRecommSearchRepository iCSRecommSearchRepository) {
        this.iCSRecommRepository = iCSRecommRepository;
        this.iCSRecommSearchRepository = iCSRecommSearchRepository;
    }

    /**
     * {@code POST  /ics-recomms} : Create a new iCSRecomm.
     *
     * @param iCSRecomm the iCSRecomm to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new iCSRecomm, or with status {@code 400 (Bad Request)} if the iCSRecomm has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/ics-recomms")
    public ResponseEntity<ICSRecomm> createICSRecomm(@RequestBody ICSRecomm iCSRecomm) throws URISyntaxException {
        log.debug("REST request to save ICSRecomm : {}", iCSRecomm);
        if (iCSRecomm.getId() != null) {
            throw new BadRequestAlertException("A new iCSRecomm cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ICSRecomm result = iCSRecommRepository.save(iCSRecomm);
        iCSRecommSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/ics-recomms/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /ics-recomms/:id} : Updates an existing iCSRecomm.
     *
     * @param id the id of the iCSRecomm to save.
     * @param iCSRecomm the iCSRecomm to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated iCSRecomm,
     * or with status {@code 400 (Bad Request)} if the iCSRecomm is not valid,
     * or with status {@code 500 (Internal Server Error)} if the iCSRecomm couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/ics-recomms/{id}")
    public ResponseEntity<ICSRecomm> updateICSRecomm(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ICSRecomm iCSRecomm
    ) throws URISyntaxException {
        log.debug("REST request to update ICSRecomm : {}, {}", id, iCSRecomm);
        if (iCSRecomm.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, iCSRecomm.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!iCSRecommRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ICSRecomm result = iCSRecommRepository.save(iCSRecomm);
        iCSRecommSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, iCSRecomm.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /ics-recomms/:id} : Partial updates given fields of an existing iCSRecomm, field will ignore if it is null
     *
     * @param id the id of the iCSRecomm to save.
     * @param iCSRecomm the iCSRecomm to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated iCSRecomm,
     * or with status {@code 400 (Bad Request)} if the iCSRecomm is not valid,
     * or with status {@code 404 (Not Found)} if the iCSRecomm is not found,
     * or with status {@code 500 (Internal Server Error)} if the iCSRecomm couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/ics-recomms/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<ICSRecomm> partialUpdateICSRecomm(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ICSRecomm iCSRecomm
    ) throws URISyntaxException {
        log.debug("REST request to partial update ICSRecomm partially : {}, {}", id, iCSRecomm);
        if (iCSRecomm.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, iCSRecomm.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!iCSRecommRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ICSRecomm> result = iCSRecommRepository
            .findById(iCSRecomm.getId())
            .map(
                existingICSRecomm -> {
                    if (iCSRecomm.getYear() != null) {
                        existingICSRecomm.setYear(iCSRecomm.getYear());
                    }
                    if (iCSRecomm.getIcsNumber() != null) {
                        existingICSRecomm.setIcsNumber(iCSRecomm.getIcsNumber());
                    }
                    if (iCSRecomm.getIcsDescr() != null) {
                        existingICSRecomm.setIcsDescr(iCSRecomm.getIcsDescr());
                    }
                    if (iCSRecomm.getTitle() != null) {
                        existingICSRecomm.setTitle(iCSRecomm.getTitle());
                    }

                    return existingICSRecomm;
                }
            )
            .map(iCSRecommRepository::save)
            .map(
                savedICSRecomm -> {
                    iCSRecommSearchRepository.save(savedICSRecomm);

                    return savedICSRecomm;
                }
            );

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, iCSRecomm.getId().toString())
        );
    }

    /**
     * {@code GET  /ics-recomms} : get all the iCSRecomms.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of iCSRecomms in body.
     */
    @GetMapping("/ics-recomms")
    public List<ICSRecomm> getAllICSRecomms() {
        log.debug("REST request to get all ICSRecomms");
        return iCSRecommRepository.findAll();
    }

    /**
     * {@code GET  /ics-recomms/:id} : get the "id" iCSRecomm.
     *
     * @param id the id of the iCSRecomm to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the iCSRecomm, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/ics-recomms/{id}")
    public ResponseEntity<ICSRecomm> getICSRecomm(@PathVariable Long id) {
        log.debug("REST request to get ICSRecomm : {}", id);
        Optional<ICSRecomm> iCSRecomm = iCSRecommRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(iCSRecomm);
    }

    /**
     * {@code DELETE  /ics-recomms/:id} : delete the "id" iCSRecomm.
     *
     * @param id the id of the iCSRecomm to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/ics-recomms/{id}")
    public ResponseEntity<Void> deleteICSRecomm(@PathVariable Long id) {
        log.debug("REST request to delete ICSRecomm : {}", id);
        iCSRecommRepository.deleteById(id);
        iCSRecommSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/ics-recomms?query=:query} : search for the iCSRecomm corresponding
     * to the query.
     *
     * @param query the query of the iCSRecomm search.
     * @return the result of the search.
     */
    @GetMapping("/_search/ics-recomms")
    public List<ICSRecomm> searchICSRecomms(@RequestParam String query) {
        log.debug("REST request to search ICSRecomms for query {}", query);
        return StreamSupport
            .stream(iCSRecommSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}

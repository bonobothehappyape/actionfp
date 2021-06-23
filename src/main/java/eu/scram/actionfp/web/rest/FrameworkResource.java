package eu.scram.actionfp.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import eu.scram.actionfp.domain.Framework;
import eu.scram.actionfp.repository.FrameworkRepository;
import eu.scram.actionfp.repository.search.FrameworkSearchRepository;
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
 * REST controller for managing {@link eu.scram.actionfp.domain.Framework}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class FrameworkResource {

    private final Logger log = LoggerFactory.getLogger(FrameworkResource.class);

    private static final String ENTITY_NAME = "framework";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FrameworkRepository frameworkRepository;

    private final FrameworkSearchRepository frameworkSearchRepository;

    public FrameworkResource(FrameworkRepository frameworkRepository, FrameworkSearchRepository frameworkSearchRepository) {
        this.frameworkRepository = frameworkRepository;
        this.frameworkSearchRepository = frameworkSearchRepository;
    }

    /**
     * {@code POST  /frameworks} : Create a new framework.
     *
     * @param framework the framework to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new framework, or with status {@code 400 (Bad Request)} if the framework has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/frameworks")
    public ResponseEntity<Framework> createFramework(@RequestBody Framework framework) throws URISyntaxException {
        log.debug("REST request to save Framework : {}", framework);
        if (framework.getId() != null) {
            throw new BadRequestAlertException("A new framework cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Framework result = frameworkRepository.save(framework);
        frameworkSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/frameworks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /frameworks/:id} : Updates an existing framework.
     *
     * @param id the id of the framework to save.
     * @param framework the framework to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated framework,
     * or with status {@code 400 (Bad Request)} if the framework is not valid,
     * or with status {@code 500 (Internal Server Error)} if the framework couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/frameworks/{id}")
    public ResponseEntity<Framework> updateFramework(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Framework framework
    ) throws URISyntaxException {
        log.debug("REST request to update Framework : {}, {}", id, framework);
        if (framework.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, framework.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!frameworkRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Framework result = frameworkRepository.save(framework);
        frameworkSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, framework.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /frameworks/:id} : Partial updates given fields of an existing framework, field will ignore if it is null
     *
     * @param id the id of the framework to save.
     * @param framework the framework to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated framework,
     * or with status {@code 400 (Bad Request)} if the framework is not valid,
     * or with status {@code 404 (Not Found)} if the framework is not found,
     * or with status {@code 500 (Internal Server Error)} if the framework couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/frameworks/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<Framework> partialUpdateFramework(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Framework framework
    ) throws URISyntaxException {
        log.debug("REST request to partial update Framework partially : {}, {}", id, framework);
        if (framework.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, framework.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!frameworkRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Framework> result = frameworkRepository
            .findById(framework.getId())
            .map(
                existingFramework -> {
                    if (framework.getYear() != null) {
                        existingFramework.setYear(framework.getYear());
                    }
                    if (framework.getName() != null) {
                        existingFramework.setName(framework.getName());
                    }
                    if (framework.getType() != null) {
                        existingFramework.setType(framework.getType());
                    }
                    if (framework.getDescription() != null) {
                        existingFramework.setDescription(framework.getDescription());
                    }

                    return existingFramework;
                }
            )
            .map(frameworkRepository::save)
            .map(
                savedFramework -> {
                    frameworkSearchRepository.save(savedFramework);

                    return savedFramework;
                }
            );

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, framework.getId().toString())
        );
    }

    /**
     * {@code GET  /frameworks} : get all the frameworks.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of frameworks in body.
     */
    @GetMapping("/frameworks")
    public List<Framework> getAllFrameworks() {
        log.debug("REST request to get all Frameworks");
        return frameworkRepository.findAll();
    }

    /**
     * {@code GET  /frameworks/:id} : get the "id" framework.
     *
     * @param id the id of the framework to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the framework, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/frameworks/{id}")
    public ResponseEntity<Framework> getFramework(@PathVariable Long id) {
        log.debug("REST request to get Framework : {}", id);
        Optional<Framework> framework = frameworkRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(framework);
    }

    /**
     * {@code DELETE  /frameworks/:id} : delete the "id" framework.
     *
     * @param id the id of the framework to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/frameworks/{id}")
    public ResponseEntity<Void> deleteFramework(@PathVariable Long id) {
        log.debug("REST request to delete Framework : {}", id);
        frameworkRepository.deleteById(id);
        frameworkSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/frameworks?query=:query} : search for the framework corresponding
     * to the query.
     *
     * @param query the query of the framework search.
     * @return the result of the search.
     */
    @GetMapping("/_search/frameworks")
    public List<Framework> searchFrameworks(@RequestParam String query) {
        log.debug("REST request to search Frameworks for query {}", query);
        return StreamSupport
            .stream(frameworkSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}

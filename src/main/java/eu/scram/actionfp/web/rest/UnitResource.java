package eu.scram.actionfp.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import eu.scram.actionfp.domain.Unit;
import eu.scram.actionfp.repository.UnitRepository;
import eu.scram.actionfp.repository.search.UnitSearchRepository;
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
 * REST controller for managing {@link eu.scram.actionfp.domain.Unit}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class UnitResource {

    private final Logger log = LoggerFactory.getLogger(UnitResource.class);

    private static final String ENTITY_NAME = "unit";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UnitRepository unitRepository;

    private final UnitSearchRepository unitSearchRepository;

    public UnitResource(UnitRepository unitRepository, UnitSearchRepository unitSearchRepository) {
        this.unitRepository = unitRepository;
        this.unitSearchRepository = unitSearchRepository;
    }

    /**
     * {@code POST  /units} : Create a new unit.
     *
     * @param unit the unit to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new unit, or with status {@code 400 (Bad Request)} if the unit has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/units")
    public ResponseEntity<Unit> createUnit(@RequestBody Unit unit) throws URISyntaxException {
        log.debug("REST request to save Unit : {}", unit);
        if (unit.getId() != null) {
            throw new BadRequestAlertException("A new unit cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Unit result = unitRepository.save(unit);
        unitSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/units/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /units/:id} : Updates an existing unit.
     *
     * @param id the id of the unit to save.
     * @param unit the unit to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated unit,
     * or with status {@code 400 (Bad Request)} if the unit is not valid,
     * or with status {@code 500 (Internal Server Error)} if the unit couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/units/{id}")
    public ResponseEntity<Unit> updateUnit(@PathVariable(value = "id", required = false) final Long id, @RequestBody Unit unit)
        throws URISyntaxException {
        log.debug("REST request to update Unit : {}, {}", id, unit);
        if (unit.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, unit.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!unitRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Unit result = unitRepository.save(unit);
        unitSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, unit.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /units/:id} : Partial updates given fields of an existing unit, field will ignore if it is null
     *
     * @param id the id of the unit to save.
     * @param unit the unit to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated unit,
     * or with status {@code 400 (Bad Request)} if the unit is not valid,
     * or with status {@code 404 (Not Found)} if the unit is not found,
     * or with status {@code 500 (Internal Server Error)} if the unit couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/units/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<Unit> partialUpdateUnit(@PathVariable(value = "id", required = false) final Long id, @RequestBody Unit unit)
        throws URISyntaxException {
        log.debug("REST request to partial update Unit partially : {}, {}", id, unit);
        if (unit.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, unit.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!unitRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Unit> result = unitRepository
            .findById(unit.getId())
            .map(
                existingUnit -> {
                    if (unit.getUnit() != null) {
                        existingUnit.setUnit(unit.getUnit());
                    }

                    return existingUnit;
                }
            )
            .map(unitRepository::save)
            .map(
                savedUnit -> {
                    unitSearchRepository.save(savedUnit);

                    return savedUnit;
                }
            );

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, unit.getId().toString())
        );
    }

    /**
     * {@code GET  /units} : get all the units.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of units in body.
     */
    @GetMapping("/units")
    public List<Unit> getAllUnits() {
        log.debug("REST request to get all Units");
        return unitRepository.findAll();
    }

    /**
     * {@code GET  /units/:id} : get the "id" unit.
     *
     * @param id the id of the unit to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the unit, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/units/{id}")
    public ResponseEntity<Unit> getUnit(@PathVariable Long id) {
        log.debug("REST request to get Unit : {}", id);
        Optional<Unit> unit = unitRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(unit);
    }

    /**
     * {@code DELETE  /units/:id} : delete the "id" unit.
     *
     * @param id the id of the unit to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/units/{id}")
    public ResponseEntity<Void> deleteUnit(@PathVariable Long id) {
        log.debug("REST request to delete Unit : {}", id);
        unitRepository.deleteById(id);
        unitSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/units?query=:query} : search for the unit corresponding
     * to the query.
     *
     * @param query the query of the unit search.
     * @return the result of the search.
     */
    @GetMapping("/_search/units")
    public List<Unit> searchUnits(@RequestParam String query) {
        log.debug("REST request to search Units for query {}", query);
        return StreamSupport.stream(unitSearchRepository.search(queryStringQuery(query)).spliterator(), false).collect(Collectors.toList());
    }
}

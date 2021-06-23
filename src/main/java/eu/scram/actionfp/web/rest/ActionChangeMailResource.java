package eu.scram.actionfp.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import eu.scram.actionfp.domain.ActionChangeMail;
import eu.scram.actionfp.repository.ActionChangeMailRepository;
import eu.scram.actionfp.repository.search.ActionChangeMailSearchRepository;
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
 * REST controller for managing {@link eu.scram.actionfp.domain.ActionChangeMail}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ActionChangeMailResource {

    private final Logger log = LoggerFactory.getLogger(ActionChangeMailResource.class);

    private static final String ENTITY_NAME = "actionChangeMail";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ActionChangeMailRepository actionChangeMailRepository;

    private final ActionChangeMailSearchRepository actionChangeMailSearchRepository;

    public ActionChangeMailResource(
        ActionChangeMailRepository actionChangeMailRepository,
        ActionChangeMailSearchRepository actionChangeMailSearchRepository
    ) {
        this.actionChangeMailRepository = actionChangeMailRepository;
        this.actionChangeMailSearchRepository = actionChangeMailSearchRepository;
    }

    /**
     * {@code POST  /action-change-mails} : Create a new actionChangeMail.
     *
     * @param actionChangeMail the actionChangeMail to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new actionChangeMail, or with status {@code 400 (Bad Request)} if the actionChangeMail has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/action-change-mails")
    public ResponseEntity<ActionChangeMail> createActionChangeMail(@RequestBody ActionChangeMail actionChangeMail)
        throws URISyntaxException {
        log.debug("REST request to save ActionChangeMail : {}", actionChangeMail);
        if (actionChangeMail.getId() != null) {
            throw new BadRequestAlertException("A new actionChangeMail cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ActionChangeMail result = actionChangeMailRepository.save(actionChangeMail);
        actionChangeMailSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/action-change-mails/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /action-change-mails/:id} : Updates an existing actionChangeMail.
     *
     * @param id the id of the actionChangeMail to save.
     * @param actionChangeMail the actionChangeMail to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated actionChangeMail,
     * or with status {@code 400 (Bad Request)} if the actionChangeMail is not valid,
     * or with status {@code 500 (Internal Server Error)} if the actionChangeMail couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/action-change-mails/{id}")
    public ResponseEntity<ActionChangeMail> updateActionChangeMail(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ActionChangeMail actionChangeMail
    ) throws URISyntaxException {
        log.debug("REST request to update ActionChangeMail : {}, {}", id, actionChangeMail);
        if (actionChangeMail.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, actionChangeMail.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!actionChangeMailRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ActionChangeMail result = actionChangeMailRepository.save(actionChangeMail);
        actionChangeMailSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, actionChangeMail.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /action-change-mails/:id} : Partial updates given fields of an existing actionChangeMail, field will ignore if it is null
     *
     * @param id the id of the actionChangeMail to save.
     * @param actionChangeMail the actionChangeMail to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated actionChangeMail,
     * or with status {@code 400 (Bad Request)} if the actionChangeMail is not valid,
     * or with status {@code 404 (Not Found)} if the actionChangeMail is not found,
     * or with status {@code 500 (Internal Server Error)} if the actionChangeMail couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/action-change-mails/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<ActionChangeMail> partialUpdateActionChangeMail(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ActionChangeMail actionChangeMail
    ) throws URISyntaxException {
        log.debug("REST request to partial update ActionChangeMail partially : {}, {}", id, actionChangeMail);
        if (actionChangeMail.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, actionChangeMail.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!actionChangeMailRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ActionChangeMail> result = actionChangeMailRepository
            .findById(actionChangeMail.getId())
            .map(
                existingActionChangeMail -> {
                    if (actionChangeMail.getActionType() != null) {
                        existingActionChangeMail.setActionType(actionChangeMail.getActionType());
                    }

                    return existingActionChangeMail;
                }
            )
            .map(actionChangeMailRepository::save)
            .map(
                savedActionChangeMail -> {
                    actionChangeMailSearchRepository.save(savedActionChangeMail);

                    return savedActionChangeMail;
                }
            );

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, actionChangeMail.getId().toString())
        );
    }

    /**
     * {@code GET  /action-change-mails} : get all the actionChangeMails.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of actionChangeMails in body.
     */
    @GetMapping("/action-change-mails")
    public List<ActionChangeMail> getAllActionChangeMails() {
        log.debug("REST request to get all ActionChangeMails");
        return actionChangeMailRepository.findAll();
    }

    /**
     * {@code GET  /action-change-mails/:id} : get the "id" actionChangeMail.
     *
     * @param id the id of the actionChangeMail to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the actionChangeMail, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/action-change-mails/{id}")
    public ResponseEntity<ActionChangeMail> getActionChangeMail(@PathVariable Long id) {
        log.debug("REST request to get ActionChangeMail : {}", id);
        Optional<ActionChangeMail> actionChangeMail = actionChangeMailRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(actionChangeMail);
    }

    /**
     * {@code DELETE  /action-change-mails/:id} : delete the "id" actionChangeMail.
     *
     * @param id the id of the actionChangeMail to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/action-change-mails/{id}")
    public ResponseEntity<Void> deleteActionChangeMail(@PathVariable Long id) {
        log.debug("REST request to delete ActionChangeMail : {}", id);
        actionChangeMailRepository.deleteById(id);
        actionChangeMailSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/action-change-mails?query=:query} : search for the actionChangeMail corresponding
     * to the query.
     *
     * @param query the query of the actionChangeMail search.
     * @return the result of the search.
     */
    @GetMapping("/_search/action-change-mails")
    public List<ActionChangeMail> searchActionChangeMails(@RequestParam String query) {
        log.debug("REST request to search ActionChangeMails for query {}", query);
        return StreamSupport
            .stream(actionChangeMailSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}

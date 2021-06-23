package eu.scram.actionfp.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import eu.scram.actionfp.domain.Action;
import eu.scram.actionfp.repository.ActionRepository;
import eu.scram.actionfp.repository.search.ActionSearchRepository;
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
 * REST controller for managing {@link eu.scram.actionfp.domain.Action}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ActionResource {

    private final Logger log = LoggerFactory.getLogger(ActionResource.class);

    private static final String ENTITY_NAME = "action";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ActionRepository actionRepository;

    private final ActionSearchRepository actionSearchRepository;

    public ActionResource(ActionRepository actionRepository, ActionSearchRepository actionSearchRepository) {
        this.actionRepository = actionRepository;
        this.actionSearchRepository = actionSearchRepository;
    }

    /**
     * {@code POST  /actions} : Create a new action.
     *
     * @param action the action to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new action, or with status {@code 400 (Bad Request)} if the action has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/actions")
    public ResponseEntity<Action> createAction(@RequestBody Action action) throws URISyntaxException {
        log.debug("REST request to save Action : {}", action);
        if (action.getId() != null) {
            throw new BadRequestAlertException("A new action cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Action result = actionRepository.save(action);
        actionSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/actions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /actions/:id} : Updates an existing action.
     *
     * @param id the id of the action to save.
     * @param action the action to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated action,
     * or with status {@code 400 (Bad Request)} if the action is not valid,
     * or with status {@code 500 (Internal Server Error)} if the action couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/actions/{id}")
    public ResponseEntity<Action> updateAction(@PathVariable(value = "id", required = false) final Long id, @RequestBody Action action)
        throws URISyntaxException {
        log.debug("REST request to update Action : {}, {}", id, action);
        if (action.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, action.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!actionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Action result = actionRepository.save(action);
        actionSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, action.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /actions/:id} : Partial updates given fields of an existing action, field will ignore if it is null
     *
     * @param id the id of the action to save.
     * @param action the action to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated action,
     * or with status {@code 400 (Bad Request)} if the action is not valid,
     * or with status {@code 404 (Not Found)} if the action is not found,
     * or with status {@code 500 (Internal Server Error)} if the action couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/actions/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<Action> partialUpdateAction(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Action action
    ) throws URISyntaxException {
        log.debug("REST request to partial update Action partially : {}, {}", id, action);
        if (action.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, action.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!actionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Action> result = actionRepository
            .findById(action.getId())
            .map(
                existingAction -> {
                    if (action.getTaskName() != null) {
                        existingAction.setTaskName(action.getTaskName());
                    }
                    if (action.getTaskDescription() != null) {
                        existingAction.setTaskDescription(action.getTaskDescription());
                    }
                    if (action.getRequiresPeriodicFollowup() != null) {
                        existingAction.setRequiresPeriodicFollowup(action.getRequiresPeriodicFollowup());
                    }
                    if (action.getInitialDeadline() != null) {
                        existingAction.setInitialDeadline(action.getInitialDeadline());
                    }
                    if (action.getUpdatedDeadline() != null) {
                        existingAction.setUpdatedDeadline(action.getUpdatedDeadline());
                    }
                    if (action.getDoneDate() != null) {
                        existingAction.setDoneDate(action.getDoneDate());
                    }
                    if (action.getVerifiedDate() != null) {
                        existingAction.setVerifiedDate(action.getVerifiedDate());
                    }

                    return existingAction;
                }
            )
            .map(actionRepository::save)
            .map(
                savedAction -> {
                    actionSearchRepository.save(savedAction);

                    return savedAction;
                }
            );

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, action.getId().toString())
        );
    }

    /**
     * {@code GET  /actions} : get all the actions.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of actions in body.
     */
    @GetMapping("/actions")
    public List<Action> getAllActions() {
        log.debug("REST request to get all Actions");
        return actionRepository.findAll();
    }

    /**
     * {@code GET  /actions/:id} : get the "id" action.
     *
     * @param id the id of the action to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the action, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/actions/{id}")
    public ResponseEntity<Action> getAction(@PathVariable Long id) {
        log.debug("REST request to get Action : {}", id);
        Optional<Action> action = actionRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(action);
    }

    /**
     * {@code DELETE  /actions/:id} : delete the "id" action.
     *
     * @param id the id of the action to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/actions/{id}")
    public ResponseEntity<Void> deleteAction(@PathVariable Long id) {
        log.debug("REST request to delete Action : {}", id);
        actionRepository.deleteById(id);
        actionSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/actions?query=:query} : search for the action corresponding
     * to the query.
     *
     * @param query the query of the action search.
     * @return the result of the search.
     */
    @GetMapping("/_search/actions")
    public List<Action> searchActions(@RequestParam String query) {
        log.debug("REST request to search Actions for query {}", query);
        return StreamSupport
            .stream(actionSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}

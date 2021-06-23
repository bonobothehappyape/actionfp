package eu.scram.actionfp.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import eu.scram.actionfp.domain.ActionComment;
import eu.scram.actionfp.repository.ActionCommentRepository;
import eu.scram.actionfp.repository.search.ActionCommentSearchRepository;
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
 * REST controller for managing {@link eu.scram.actionfp.domain.ActionComment}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ActionCommentResource {

    private final Logger log = LoggerFactory.getLogger(ActionCommentResource.class);

    private static final String ENTITY_NAME = "actionComment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ActionCommentRepository actionCommentRepository;

    private final ActionCommentSearchRepository actionCommentSearchRepository;

    public ActionCommentResource(
        ActionCommentRepository actionCommentRepository,
        ActionCommentSearchRepository actionCommentSearchRepository
    ) {
        this.actionCommentRepository = actionCommentRepository;
        this.actionCommentSearchRepository = actionCommentSearchRepository;
    }

    /**
     * {@code POST  /action-comments} : Create a new actionComment.
     *
     * @param actionComment the actionComment to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new actionComment, or with status {@code 400 (Bad Request)} if the actionComment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/action-comments")
    public ResponseEntity<ActionComment> createActionComment(@RequestBody ActionComment actionComment) throws URISyntaxException {
        log.debug("REST request to save ActionComment : {}", actionComment);
        if (actionComment.getId() != null) {
            throw new BadRequestAlertException("A new actionComment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ActionComment result = actionCommentRepository.save(actionComment);
        actionCommentSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/action-comments/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /action-comments/:id} : Updates an existing actionComment.
     *
     * @param id the id of the actionComment to save.
     * @param actionComment the actionComment to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated actionComment,
     * or with status {@code 400 (Bad Request)} if the actionComment is not valid,
     * or with status {@code 500 (Internal Server Error)} if the actionComment couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/action-comments/{id}")
    public ResponseEntity<ActionComment> updateActionComment(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ActionComment actionComment
    ) throws URISyntaxException {
        log.debug("REST request to update ActionComment : {}, {}", id, actionComment);
        if (actionComment.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, actionComment.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!actionCommentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ActionComment result = actionCommentRepository.save(actionComment);
        actionCommentSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, actionComment.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /action-comments/:id} : Partial updates given fields of an existing actionComment, field will ignore if it is null
     *
     * @param id the id of the actionComment to save.
     * @param actionComment the actionComment to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated actionComment,
     * or with status {@code 400 (Bad Request)} if the actionComment is not valid,
     * or with status {@code 404 (Not Found)} if the actionComment is not found,
     * or with status {@code 500 (Internal Server Error)} if the actionComment couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/action-comments/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<ActionComment> partialUpdateActionComment(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ActionComment actionComment
    ) throws URISyntaxException {
        log.debug("REST request to partial update ActionComment partially : {}, {}", id, actionComment);
        if (actionComment.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, actionComment.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!actionCommentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ActionComment> result = actionCommentRepository
            .findById(actionComment.getId())
            .map(
                existingActionComment -> {
                    if (actionComment.getComment() != null) {
                        existingActionComment.setComment(actionComment.getComment());
                    }

                    return existingActionComment;
                }
            )
            .map(actionCommentRepository::save)
            .map(
                savedActionComment -> {
                    actionCommentSearchRepository.save(savedActionComment);

                    return savedActionComment;
                }
            );

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, actionComment.getId().toString())
        );
    }

    /**
     * {@code GET  /action-comments} : get all the actionComments.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of actionComments in body.
     */
    @GetMapping("/action-comments")
    public List<ActionComment> getAllActionComments() {
        log.debug("REST request to get all ActionComments");
        return actionCommentRepository.findAll();
    }

    /**
     * {@code GET  /action-comments/:id} : get the "id" actionComment.
     *
     * @param id the id of the actionComment to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the actionComment, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/action-comments/{id}")
    public ResponseEntity<ActionComment> getActionComment(@PathVariable Long id) {
        log.debug("REST request to get ActionComment : {}", id);
        Optional<ActionComment> actionComment = actionCommentRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(actionComment);
    }

    /**
     * {@code DELETE  /action-comments/:id} : delete the "id" actionComment.
     *
     * @param id the id of the actionComment to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/action-comments/{id}")
    public ResponseEntity<Void> deleteActionComment(@PathVariable Long id) {
        log.debug("REST request to delete ActionComment : {}", id);
        actionCommentRepository.deleteById(id);
        actionCommentSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/action-comments?query=:query} : search for the actionComment corresponding
     * to the query.
     *
     * @param query the query of the actionComment search.
     * @return the result of the search.
     */
    @GetMapping("/_search/action-comments")
    public List<ActionComment> searchActionComments(@RequestParam String query) {
        log.debug("REST request to search ActionComments for query {}", query);
        return StreamSupport
            .stream(actionCommentSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}

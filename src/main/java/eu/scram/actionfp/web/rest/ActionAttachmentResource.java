package eu.scram.actionfp.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import eu.scram.actionfp.domain.ActionAttachment;
import eu.scram.actionfp.repository.ActionAttachmentRepository;
import eu.scram.actionfp.repository.search.ActionAttachmentSearchRepository;
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
 * REST controller for managing {@link eu.scram.actionfp.domain.ActionAttachment}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ActionAttachmentResource {

    private final Logger log = LoggerFactory.getLogger(ActionAttachmentResource.class);

    private static final String ENTITY_NAME = "actionAttachment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ActionAttachmentRepository actionAttachmentRepository;

    private final ActionAttachmentSearchRepository actionAttachmentSearchRepository;

    public ActionAttachmentResource(
        ActionAttachmentRepository actionAttachmentRepository,
        ActionAttachmentSearchRepository actionAttachmentSearchRepository
    ) {
        this.actionAttachmentRepository = actionAttachmentRepository;
        this.actionAttachmentSearchRepository = actionAttachmentSearchRepository;
    }

    /**
     * {@code POST  /action-attachments} : Create a new actionAttachment.
     *
     * @param actionAttachment the actionAttachment to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new actionAttachment, or with status {@code 400 (Bad Request)} if the actionAttachment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/action-attachments")
    public ResponseEntity<ActionAttachment> createActionAttachment(@RequestBody ActionAttachment actionAttachment)
        throws URISyntaxException {
        log.debug("REST request to save ActionAttachment : {}", actionAttachment);
        if (actionAttachment.getId() != null) {
            throw new BadRequestAlertException("A new actionAttachment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ActionAttachment result = actionAttachmentRepository.save(actionAttachment);
        actionAttachmentSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/action-attachments/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /action-attachments/:id} : Updates an existing actionAttachment.
     *
     * @param id the id of the actionAttachment to save.
     * @param actionAttachment the actionAttachment to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated actionAttachment,
     * or with status {@code 400 (Bad Request)} if the actionAttachment is not valid,
     * or with status {@code 500 (Internal Server Error)} if the actionAttachment couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/action-attachments/{id}")
    public ResponseEntity<ActionAttachment> updateActionAttachment(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ActionAttachment actionAttachment
    ) throws URISyntaxException {
        log.debug("REST request to update ActionAttachment : {}, {}", id, actionAttachment);
        if (actionAttachment.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, actionAttachment.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!actionAttachmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ActionAttachment result = actionAttachmentRepository.save(actionAttachment);
        actionAttachmentSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, actionAttachment.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /action-attachments/:id} : Partial updates given fields of an existing actionAttachment, field will ignore if it is null
     *
     * @param id the id of the actionAttachment to save.
     * @param actionAttachment the actionAttachment to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated actionAttachment,
     * or with status {@code 400 (Bad Request)} if the actionAttachment is not valid,
     * or with status {@code 404 (Not Found)} if the actionAttachment is not found,
     * or with status {@code 500 (Internal Server Error)} if the actionAttachment couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/action-attachments/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<ActionAttachment> partialUpdateActionAttachment(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ActionAttachment actionAttachment
    ) throws URISyntaxException {
        log.debug("REST request to partial update ActionAttachment partially : {}, {}", id, actionAttachment);
        if (actionAttachment.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, actionAttachment.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!actionAttachmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ActionAttachment> result = actionAttachmentRepository
            .findById(actionAttachment.getId())
            .map(
                existingActionAttachment -> {
                    if (actionAttachment.getName() != null) {
                        existingActionAttachment.setName(actionAttachment.getName());
                    }
                    if (actionAttachment.getMimeType() != null) {
                        existingActionAttachment.setMimeType(actionAttachment.getMimeType());
                    }
                    if (actionAttachment.getAttachedFile() != null) {
                        existingActionAttachment.setAttachedFile(actionAttachment.getAttachedFile());
                    }
                    if (actionAttachment.getAttachedFileContentType() != null) {
                        existingActionAttachment.setAttachedFileContentType(actionAttachment.getAttachedFileContentType());
                    }
                    if (actionAttachment.getUrl() != null) {
                        existingActionAttachment.setUrl(actionAttachment.getUrl());
                    }

                    return existingActionAttachment;
                }
            )
            .map(actionAttachmentRepository::save)
            .map(
                savedActionAttachment -> {
                    actionAttachmentSearchRepository.save(savedActionAttachment);

                    return savedActionAttachment;
                }
            );

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, actionAttachment.getId().toString())
        );
    }

    /**
     * {@code GET  /action-attachments} : get all the actionAttachments.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of actionAttachments in body.
     */
    @GetMapping("/action-attachments")
    public List<ActionAttachment> getAllActionAttachments() {
        log.debug("REST request to get all ActionAttachments");
        return actionAttachmentRepository.findAll();
    }

    /**
     * {@code GET  /action-attachments/:id} : get the "id" actionAttachment.
     *
     * @param id the id of the actionAttachment to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the actionAttachment, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/action-attachments/{id}")
    public ResponseEntity<ActionAttachment> getActionAttachment(@PathVariable Long id) {
        log.debug("REST request to get ActionAttachment : {}", id);
        Optional<ActionAttachment> actionAttachment = actionAttachmentRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(actionAttachment);
    }

    /**
     * {@code DELETE  /action-attachments/:id} : delete the "id" actionAttachment.
     *
     * @param id the id of the actionAttachment to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/action-attachments/{id}")
    public ResponseEntity<Void> deleteActionAttachment(@PathVariable Long id) {
        log.debug("REST request to delete ActionAttachment : {}", id);
        actionAttachmentRepository.deleteById(id);
        actionAttachmentSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/action-attachments?query=:query} : search for the actionAttachment corresponding
     * to the query.
     *
     * @param query the query of the actionAttachment search.
     * @return the result of the search.
     */
    @GetMapping("/_search/action-attachments")
    public List<ActionAttachment> searchActionAttachments(@RequestParam String query) {
        log.debug("REST request to search ActionAttachments for query {}", query);
        return StreamSupport
            .stream(actionAttachmentSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}

package eu.scram.actionfp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import eu.scram.actionfp.IntegrationTest;
import eu.scram.actionfp.domain.ActionComment;
import eu.scram.actionfp.repository.ActionCommentRepository;
import eu.scram.actionfp.repository.search.ActionCommentSearchRepository;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ActionCommentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ActionCommentResourceIT {

    private static final String DEFAULT_COMMENT = "AAAAAAAAAA";
    private static final String UPDATED_COMMENT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/action-comments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/action-comments";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ActionCommentRepository actionCommentRepository;

    /**
     * This repository is mocked in the eu.scram.actionfp.repository.search test package.
     *
     * @see eu.scram.actionfp.repository.search.ActionCommentSearchRepositoryMockConfiguration
     */
    @Autowired
    private ActionCommentSearchRepository mockActionCommentSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restActionCommentMockMvc;

    private ActionComment actionComment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ActionComment createEntity(EntityManager em) {
        ActionComment actionComment = new ActionComment().comment(DEFAULT_COMMENT);
        return actionComment;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ActionComment createUpdatedEntity(EntityManager em) {
        ActionComment actionComment = new ActionComment().comment(UPDATED_COMMENT);
        return actionComment;
    }

    @BeforeEach
    public void initTest() {
        actionComment = createEntity(em);
    }

    @Test
    @Transactional
    void createActionComment() throws Exception {
        int databaseSizeBeforeCreate = actionCommentRepository.findAll().size();
        // Create the ActionComment
        restActionCommentMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(actionComment))
            )
            .andExpect(status().isCreated());

        // Validate the ActionComment in the database
        List<ActionComment> actionCommentList = actionCommentRepository.findAll();
        assertThat(actionCommentList).hasSize(databaseSizeBeforeCreate + 1);
        ActionComment testActionComment = actionCommentList.get(actionCommentList.size() - 1);
        assertThat(testActionComment.getComment()).isEqualTo(DEFAULT_COMMENT);

        // Validate the ActionComment in Elasticsearch
        verify(mockActionCommentSearchRepository, times(1)).save(testActionComment);
    }

    @Test
    @Transactional
    void createActionCommentWithExistingId() throws Exception {
        // Create the ActionComment with an existing ID
        actionComment.setId(1L);

        int databaseSizeBeforeCreate = actionCommentRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restActionCommentMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(actionComment))
            )
            .andExpect(status().isBadRequest());

        // Validate the ActionComment in the database
        List<ActionComment> actionCommentList = actionCommentRepository.findAll();
        assertThat(actionCommentList).hasSize(databaseSizeBeforeCreate);

        // Validate the ActionComment in Elasticsearch
        verify(mockActionCommentSearchRepository, times(0)).save(actionComment);
    }

    @Test
    @Transactional
    void getAllActionComments() throws Exception {
        // Initialize the database
        actionCommentRepository.saveAndFlush(actionComment);

        // Get all the actionCommentList
        restActionCommentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(actionComment.getId().intValue())))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT)));
    }

    @Test
    @Transactional
    void getActionComment() throws Exception {
        // Initialize the database
        actionCommentRepository.saveAndFlush(actionComment);

        // Get the actionComment
        restActionCommentMockMvc
            .perform(get(ENTITY_API_URL_ID, actionComment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(actionComment.getId().intValue()))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT));
    }

    @Test
    @Transactional
    void getNonExistingActionComment() throws Exception {
        // Get the actionComment
        restActionCommentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewActionComment() throws Exception {
        // Initialize the database
        actionCommentRepository.saveAndFlush(actionComment);

        int databaseSizeBeforeUpdate = actionCommentRepository.findAll().size();

        // Update the actionComment
        ActionComment updatedActionComment = actionCommentRepository.findById(actionComment.getId()).get();
        // Disconnect from session so that the updates on updatedActionComment are not directly saved in db
        em.detach(updatedActionComment);
        updatedActionComment.comment(UPDATED_COMMENT);

        restActionCommentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedActionComment.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedActionComment))
            )
            .andExpect(status().isOk());

        // Validate the ActionComment in the database
        List<ActionComment> actionCommentList = actionCommentRepository.findAll();
        assertThat(actionCommentList).hasSize(databaseSizeBeforeUpdate);
        ActionComment testActionComment = actionCommentList.get(actionCommentList.size() - 1);
        assertThat(testActionComment.getComment()).isEqualTo(UPDATED_COMMENT);

        // Validate the ActionComment in Elasticsearch
        verify(mockActionCommentSearchRepository).save(testActionComment);
    }

    @Test
    @Transactional
    void putNonExistingActionComment() throws Exception {
        int databaseSizeBeforeUpdate = actionCommentRepository.findAll().size();
        actionComment.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restActionCommentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, actionComment.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(actionComment))
            )
            .andExpect(status().isBadRequest());

        // Validate the ActionComment in the database
        List<ActionComment> actionCommentList = actionCommentRepository.findAll();
        assertThat(actionCommentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ActionComment in Elasticsearch
        verify(mockActionCommentSearchRepository, times(0)).save(actionComment);
    }

    @Test
    @Transactional
    void putWithIdMismatchActionComment() throws Exception {
        int databaseSizeBeforeUpdate = actionCommentRepository.findAll().size();
        actionComment.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActionCommentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(actionComment))
            )
            .andExpect(status().isBadRequest());

        // Validate the ActionComment in the database
        List<ActionComment> actionCommentList = actionCommentRepository.findAll();
        assertThat(actionCommentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ActionComment in Elasticsearch
        verify(mockActionCommentSearchRepository, times(0)).save(actionComment);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamActionComment() throws Exception {
        int databaseSizeBeforeUpdate = actionCommentRepository.findAll().size();
        actionComment.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActionCommentMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(actionComment))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ActionComment in the database
        List<ActionComment> actionCommentList = actionCommentRepository.findAll();
        assertThat(actionCommentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ActionComment in Elasticsearch
        verify(mockActionCommentSearchRepository, times(0)).save(actionComment);
    }

    @Test
    @Transactional
    void partialUpdateActionCommentWithPatch() throws Exception {
        // Initialize the database
        actionCommentRepository.saveAndFlush(actionComment);

        int databaseSizeBeforeUpdate = actionCommentRepository.findAll().size();

        // Update the actionComment using partial update
        ActionComment partialUpdatedActionComment = new ActionComment();
        partialUpdatedActionComment.setId(actionComment.getId());

        partialUpdatedActionComment.comment(UPDATED_COMMENT);

        restActionCommentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedActionComment.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedActionComment))
            )
            .andExpect(status().isOk());

        // Validate the ActionComment in the database
        List<ActionComment> actionCommentList = actionCommentRepository.findAll();
        assertThat(actionCommentList).hasSize(databaseSizeBeforeUpdate);
        ActionComment testActionComment = actionCommentList.get(actionCommentList.size() - 1);
        assertThat(testActionComment.getComment()).isEqualTo(UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void fullUpdateActionCommentWithPatch() throws Exception {
        // Initialize the database
        actionCommentRepository.saveAndFlush(actionComment);

        int databaseSizeBeforeUpdate = actionCommentRepository.findAll().size();

        // Update the actionComment using partial update
        ActionComment partialUpdatedActionComment = new ActionComment();
        partialUpdatedActionComment.setId(actionComment.getId());

        partialUpdatedActionComment.comment(UPDATED_COMMENT);

        restActionCommentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedActionComment.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedActionComment))
            )
            .andExpect(status().isOk());

        // Validate the ActionComment in the database
        List<ActionComment> actionCommentList = actionCommentRepository.findAll();
        assertThat(actionCommentList).hasSize(databaseSizeBeforeUpdate);
        ActionComment testActionComment = actionCommentList.get(actionCommentList.size() - 1);
        assertThat(testActionComment.getComment()).isEqualTo(UPDATED_COMMENT);
    }

    @Test
    @Transactional
    void patchNonExistingActionComment() throws Exception {
        int databaseSizeBeforeUpdate = actionCommentRepository.findAll().size();
        actionComment.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restActionCommentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, actionComment.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(actionComment))
            )
            .andExpect(status().isBadRequest());

        // Validate the ActionComment in the database
        List<ActionComment> actionCommentList = actionCommentRepository.findAll();
        assertThat(actionCommentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ActionComment in Elasticsearch
        verify(mockActionCommentSearchRepository, times(0)).save(actionComment);
    }

    @Test
    @Transactional
    void patchWithIdMismatchActionComment() throws Exception {
        int databaseSizeBeforeUpdate = actionCommentRepository.findAll().size();
        actionComment.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActionCommentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(actionComment))
            )
            .andExpect(status().isBadRequest());

        // Validate the ActionComment in the database
        List<ActionComment> actionCommentList = actionCommentRepository.findAll();
        assertThat(actionCommentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ActionComment in Elasticsearch
        verify(mockActionCommentSearchRepository, times(0)).save(actionComment);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamActionComment() throws Exception {
        int databaseSizeBeforeUpdate = actionCommentRepository.findAll().size();
        actionComment.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActionCommentMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(actionComment))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ActionComment in the database
        List<ActionComment> actionCommentList = actionCommentRepository.findAll();
        assertThat(actionCommentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ActionComment in Elasticsearch
        verify(mockActionCommentSearchRepository, times(0)).save(actionComment);
    }

    @Test
    @Transactional
    void deleteActionComment() throws Exception {
        // Initialize the database
        actionCommentRepository.saveAndFlush(actionComment);

        int databaseSizeBeforeDelete = actionCommentRepository.findAll().size();

        // Delete the actionComment
        restActionCommentMockMvc
            .perform(delete(ENTITY_API_URL_ID, actionComment.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ActionComment> actionCommentList = actionCommentRepository.findAll();
        assertThat(actionCommentList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the ActionComment in Elasticsearch
        verify(mockActionCommentSearchRepository, times(1)).deleteById(actionComment.getId());
    }

    @Test
    @Transactional
    void searchActionComment() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        actionCommentRepository.saveAndFlush(actionComment);
        when(mockActionCommentSearchRepository.search(queryStringQuery("id:" + actionComment.getId())))
            .thenReturn(Collections.singletonList(actionComment));

        // Search the actionComment
        restActionCommentMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + actionComment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(actionComment.getId().intValue())))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT)));
    }
}

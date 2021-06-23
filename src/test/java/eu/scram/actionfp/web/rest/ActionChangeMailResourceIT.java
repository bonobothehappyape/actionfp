package eu.scram.actionfp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import eu.scram.actionfp.IntegrationTest;
import eu.scram.actionfp.domain.ActionChangeMail;
import eu.scram.actionfp.repository.ActionChangeMailRepository;
import eu.scram.actionfp.repository.search.ActionChangeMailSearchRepository;
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
 * Integration tests for the {@link ActionChangeMailResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ActionChangeMailResourceIT {

    private static final String DEFAULT_ACTION_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_ACTION_TYPE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/action-change-mails";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/action-change-mails";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ActionChangeMailRepository actionChangeMailRepository;

    /**
     * This repository is mocked in the eu.scram.actionfp.repository.search test package.
     *
     * @see eu.scram.actionfp.repository.search.ActionChangeMailSearchRepositoryMockConfiguration
     */
    @Autowired
    private ActionChangeMailSearchRepository mockActionChangeMailSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restActionChangeMailMockMvc;

    private ActionChangeMail actionChangeMail;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ActionChangeMail createEntity(EntityManager em) {
        ActionChangeMail actionChangeMail = new ActionChangeMail().actionType(DEFAULT_ACTION_TYPE);
        return actionChangeMail;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ActionChangeMail createUpdatedEntity(EntityManager em) {
        ActionChangeMail actionChangeMail = new ActionChangeMail().actionType(UPDATED_ACTION_TYPE);
        return actionChangeMail;
    }

    @BeforeEach
    public void initTest() {
        actionChangeMail = createEntity(em);
    }

    @Test
    @Transactional
    void createActionChangeMail() throws Exception {
        int databaseSizeBeforeCreate = actionChangeMailRepository.findAll().size();
        // Create the ActionChangeMail
        restActionChangeMailMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(actionChangeMail))
            )
            .andExpect(status().isCreated());

        // Validate the ActionChangeMail in the database
        List<ActionChangeMail> actionChangeMailList = actionChangeMailRepository.findAll();
        assertThat(actionChangeMailList).hasSize(databaseSizeBeforeCreate + 1);
        ActionChangeMail testActionChangeMail = actionChangeMailList.get(actionChangeMailList.size() - 1);
        assertThat(testActionChangeMail.getActionType()).isEqualTo(DEFAULT_ACTION_TYPE);

        // Validate the ActionChangeMail in Elasticsearch
        verify(mockActionChangeMailSearchRepository, times(1)).save(testActionChangeMail);
    }

    @Test
    @Transactional
    void createActionChangeMailWithExistingId() throws Exception {
        // Create the ActionChangeMail with an existing ID
        actionChangeMail.setId(1L);

        int databaseSizeBeforeCreate = actionChangeMailRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restActionChangeMailMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(actionChangeMail))
            )
            .andExpect(status().isBadRequest());

        // Validate the ActionChangeMail in the database
        List<ActionChangeMail> actionChangeMailList = actionChangeMailRepository.findAll();
        assertThat(actionChangeMailList).hasSize(databaseSizeBeforeCreate);

        // Validate the ActionChangeMail in Elasticsearch
        verify(mockActionChangeMailSearchRepository, times(0)).save(actionChangeMail);
    }

    @Test
    @Transactional
    void getAllActionChangeMails() throws Exception {
        // Initialize the database
        actionChangeMailRepository.saveAndFlush(actionChangeMail);

        // Get all the actionChangeMailList
        restActionChangeMailMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(actionChangeMail.getId().intValue())))
            .andExpect(jsonPath("$.[*].actionType").value(hasItem(DEFAULT_ACTION_TYPE)));
    }

    @Test
    @Transactional
    void getActionChangeMail() throws Exception {
        // Initialize the database
        actionChangeMailRepository.saveAndFlush(actionChangeMail);

        // Get the actionChangeMail
        restActionChangeMailMockMvc
            .perform(get(ENTITY_API_URL_ID, actionChangeMail.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(actionChangeMail.getId().intValue()))
            .andExpect(jsonPath("$.actionType").value(DEFAULT_ACTION_TYPE));
    }

    @Test
    @Transactional
    void getNonExistingActionChangeMail() throws Exception {
        // Get the actionChangeMail
        restActionChangeMailMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewActionChangeMail() throws Exception {
        // Initialize the database
        actionChangeMailRepository.saveAndFlush(actionChangeMail);

        int databaseSizeBeforeUpdate = actionChangeMailRepository.findAll().size();

        // Update the actionChangeMail
        ActionChangeMail updatedActionChangeMail = actionChangeMailRepository.findById(actionChangeMail.getId()).get();
        // Disconnect from session so that the updates on updatedActionChangeMail are not directly saved in db
        em.detach(updatedActionChangeMail);
        updatedActionChangeMail.actionType(UPDATED_ACTION_TYPE);

        restActionChangeMailMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedActionChangeMail.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedActionChangeMail))
            )
            .andExpect(status().isOk());

        // Validate the ActionChangeMail in the database
        List<ActionChangeMail> actionChangeMailList = actionChangeMailRepository.findAll();
        assertThat(actionChangeMailList).hasSize(databaseSizeBeforeUpdate);
        ActionChangeMail testActionChangeMail = actionChangeMailList.get(actionChangeMailList.size() - 1);
        assertThat(testActionChangeMail.getActionType()).isEqualTo(UPDATED_ACTION_TYPE);

        // Validate the ActionChangeMail in Elasticsearch
        verify(mockActionChangeMailSearchRepository).save(testActionChangeMail);
    }

    @Test
    @Transactional
    void putNonExistingActionChangeMail() throws Exception {
        int databaseSizeBeforeUpdate = actionChangeMailRepository.findAll().size();
        actionChangeMail.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restActionChangeMailMockMvc
            .perform(
                put(ENTITY_API_URL_ID, actionChangeMail.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(actionChangeMail))
            )
            .andExpect(status().isBadRequest());

        // Validate the ActionChangeMail in the database
        List<ActionChangeMail> actionChangeMailList = actionChangeMailRepository.findAll();
        assertThat(actionChangeMailList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ActionChangeMail in Elasticsearch
        verify(mockActionChangeMailSearchRepository, times(0)).save(actionChangeMail);
    }

    @Test
    @Transactional
    void putWithIdMismatchActionChangeMail() throws Exception {
        int databaseSizeBeforeUpdate = actionChangeMailRepository.findAll().size();
        actionChangeMail.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActionChangeMailMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(actionChangeMail))
            )
            .andExpect(status().isBadRequest());

        // Validate the ActionChangeMail in the database
        List<ActionChangeMail> actionChangeMailList = actionChangeMailRepository.findAll();
        assertThat(actionChangeMailList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ActionChangeMail in Elasticsearch
        verify(mockActionChangeMailSearchRepository, times(0)).save(actionChangeMail);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamActionChangeMail() throws Exception {
        int databaseSizeBeforeUpdate = actionChangeMailRepository.findAll().size();
        actionChangeMail.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActionChangeMailMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(actionChangeMail))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ActionChangeMail in the database
        List<ActionChangeMail> actionChangeMailList = actionChangeMailRepository.findAll();
        assertThat(actionChangeMailList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ActionChangeMail in Elasticsearch
        verify(mockActionChangeMailSearchRepository, times(0)).save(actionChangeMail);
    }

    @Test
    @Transactional
    void partialUpdateActionChangeMailWithPatch() throws Exception {
        // Initialize the database
        actionChangeMailRepository.saveAndFlush(actionChangeMail);

        int databaseSizeBeforeUpdate = actionChangeMailRepository.findAll().size();

        // Update the actionChangeMail using partial update
        ActionChangeMail partialUpdatedActionChangeMail = new ActionChangeMail();
        partialUpdatedActionChangeMail.setId(actionChangeMail.getId());

        partialUpdatedActionChangeMail.actionType(UPDATED_ACTION_TYPE);

        restActionChangeMailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedActionChangeMail.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedActionChangeMail))
            )
            .andExpect(status().isOk());

        // Validate the ActionChangeMail in the database
        List<ActionChangeMail> actionChangeMailList = actionChangeMailRepository.findAll();
        assertThat(actionChangeMailList).hasSize(databaseSizeBeforeUpdate);
        ActionChangeMail testActionChangeMail = actionChangeMailList.get(actionChangeMailList.size() - 1);
        assertThat(testActionChangeMail.getActionType()).isEqualTo(UPDATED_ACTION_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateActionChangeMailWithPatch() throws Exception {
        // Initialize the database
        actionChangeMailRepository.saveAndFlush(actionChangeMail);

        int databaseSizeBeforeUpdate = actionChangeMailRepository.findAll().size();

        // Update the actionChangeMail using partial update
        ActionChangeMail partialUpdatedActionChangeMail = new ActionChangeMail();
        partialUpdatedActionChangeMail.setId(actionChangeMail.getId());

        partialUpdatedActionChangeMail.actionType(UPDATED_ACTION_TYPE);

        restActionChangeMailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedActionChangeMail.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedActionChangeMail))
            )
            .andExpect(status().isOk());

        // Validate the ActionChangeMail in the database
        List<ActionChangeMail> actionChangeMailList = actionChangeMailRepository.findAll();
        assertThat(actionChangeMailList).hasSize(databaseSizeBeforeUpdate);
        ActionChangeMail testActionChangeMail = actionChangeMailList.get(actionChangeMailList.size() - 1);
        assertThat(testActionChangeMail.getActionType()).isEqualTo(UPDATED_ACTION_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingActionChangeMail() throws Exception {
        int databaseSizeBeforeUpdate = actionChangeMailRepository.findAll().size();
        actionChangeMail.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restActionChangeMailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, actionChangeMail.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(actionChangeMail))
            )
            .andExpect(status().isBadRequest());

        // Validate the ActionChangeMail in the database
        List<ActionChangeMail> actionChangeMailList = actionChangeMailRepository.findAll();
        assertThat(actionChangeMailList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ActionChangeMail in Elasticsearch
        verify(mockActionChangeMailSearchRepository, times(0)).save(actionChangeMail);
    }

    @Test
    @Transactional
    void patchWithIdMismatchActionChangeMail() throws Exception {
        int databaseSizeBeforeUpdate = actionChangeMailRepository.findAll().size();
        actionChangeMail.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActionChangeMailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(actionChangeMail))
            )
            .andExpect(status().isBadRequest());

        // Validate the ActionChangeMail in the database
        List<ActionChangeMail> actionChangeMailList = actionChangeMailRepository.findAll();
        assertThat(actionChangeMailList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ActionChangeMail in Elasticsearch
        verify(mockActionChangeMailSearchRepository, times(0)).save(actionChangeMail);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamActionChangeMail() throws Exception {
        int databaseSizeBeforeUpdate = actionChangeMailRepository.findAll().size();
        actionChangeMail.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActionChangeMailMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(actionChangeMail))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ActionChangeMail in the database
        List<ActionChangeMail> actionChangeMailList = actionChangeMailRepository.findAll();
        assertThat(actionChangeMailList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ActionChangeMail in Elasticsearch
        verify(mockActionChangeMailSearchRepository, times(0)).save(actionChangeMail);
    }

    @Test
    @Transactional
    void deleteActionChangeMail() throws Exception {
        // Initialize the database
        actionChangeMailRepository.saveAndFlush(actionChangeMail);

        int databaseSizeBeforeDelete = actionChangeMailRepository.findAll().size();

        // Delete the actionChangeMail
        restActionChangeMailMockMvc
            .perform(delete(ENTITY_API_URL_ID, actionChangeMail.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ActionChangeMail> actionChangeMailList = actionChangeMailRepository.findAll();
        assertThat(actionChangeMailList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the ActionChangeMail in Elasticsearch
        verify(mockActionChangeMailSearchRepository, times(1)).deleteById(actionChangeMail.getId());
    }

    @Test
    @Transactional
    void searchActionChangeMail() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        actionChangeMailRepository.saveAndFlush(actionChangeMail);
        when(mockActionChangeMailSearchRepository.search(queryStringQuery("id:" + actionChangeMail.getId())))
            .thenReturn(Collections.singletonList(actionChangeMail));

        // Search the actionChangeMail
        restActionChangeMailMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + actionChangeMail.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(actionChangeMail.getId().intValue())))
            .andExpect(jsonPath("$.[*].actionType").value(hasItem(DEFAULT_ACTION_TYPE)));
    }
}

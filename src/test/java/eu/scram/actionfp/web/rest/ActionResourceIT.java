package eu.scram.actionfp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import eu.scram.actionfp.IntegrationTest;
import eu.scram.actionfp.domain.Action;
import eu.scram.actionfp.repository.ActionRepository;
import eu.scram.actionfp.repository.search.ActionSearchRepository;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link ActionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ActionResourceIT {

    private static final String DEFAULT_TASK_NAME = "AAAAAAAAAA";
    private static final String UPDATED_TASK_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_TASK_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_TASK_DESCRIPTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_REQUIRES_PERIODIC_FOLLOWUP = false;
    private static final Boolean UPDATED_REQUIRES_PERIODIC_FOLLOWUP = true;

    private static final LocalDate DEFAULT_INITIAL_DEADLINE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_INITIAL_DEADLINE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_UPDATED_DEADLINE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_UPDATED_DEADLINE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_DONE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DONE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_VERIFIED_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_VERIFIED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/actions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/actions";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ActionRepository actionRepository;

    /**
     * This repository is mocked in the eu.scram.actionfp.repository.search test package.
     *
     * @see eu.scram.actionfp.repository.search.ActionSearchRepositoryMockConfiguration
     */
    @Autowired
    private ActionSearchRepository mockActionSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restActionMockMvc;

    private Action action;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Action createEntity(EntityManager em) {
        Action action = new Action()
            .taskName(DEFAULT_TASK_NAME)
            .taskDescription(DEFAULT_TASK_DESCRIPTION)
            .requiresPeriodicFollowup(DEFAULT_REQUIRES_PERIODIC_FOLLOWUP)
            .initialDeadline(DEFAULT_INITIAL_DEADLINE)
            .updatedDeadline(DEFAULT_UPDATED_DEADLINE)
            .doneDate(DEFAULT_DONE_DATE)
            .verifiedDate(DEFAULT_VERIFIED_DATE);
        return action;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Action createUpdatedEntity(EntityManager em) {
        Action action = new Action()
            .taskName(UPDATED_TASK_NAME)
            .taskDescription(UPDATED_TASK_DESCRIPTION)
            .requiresPeriodicFollowup(UPDATED_REQUIRES_PERIODIC_FOLLOWUP)
            .initialDeadline(UPDATED_INITIAL_DEADLINE)
            .updatedDeadline(UPDATED_UPDATED_DEADLINE)
            .doneDate(UPDATED_DONE_DATE)
            .verifiedDate(UPDATED_VERIFIED_DATE);
        return action;
    }

    @BeforeEach
    public void initTest() {
        action = createEntity(em);
    }

    @Test
    @Transactional
    void createAction() throws Exception {
        int databaseSizeBeforeCreate = actionRepository.findAll().size();
        // Create the Action
        restActionMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(action))
            )
            .andExpect(status().isCreated());

        // Validate the Action in the database
        List<Action> actionList = actionRepository.findAll();
        assertThat(actionList).hasSize(databaseSizeBeforeCreate + 1);
        Action testAction = actionList.get(actionList.size() - 1);
        assertThat(testAction.getTaskName()).isEqualTo(DEFAULT_TASK_NAME);
        assertThat(testAction.getTaskDescription()).isEqualTo(DEFAULT_TASK_DESCRIPTION);
        assertThat(testAction.getRequiresPeriodicFollowup()).isEqualTo(DEFAULT_REQUIRES_PERIODIC_FOLLOWUP);
        assertThat(testAction.getInitialDeadline()).isEqualTo(DEFAULT_INITIAL_DEADLINE);
        assertThat(testAction.getUpdatedDeadline()).isEqualTo(DEFAULT_UPDATED_DEADLINE);
        assertThat(testAction.getDoneDate()).isEqualTo(DEFAULT_DONE_DATE);
        assertThat(testAction.getVerifiedDate()).isEqualTo(DEFAULT_VERIFIED_DATE);

        // Validate the Action in Elasticsearch
        verify(mockActionSearchRepository, times(1)).save(testAction);
    }

    @Test
    @Transactional
    void createActionWithExistingId() throws Exception {
        // Create the Action with an existing ID
        action.setId(1L);

        int databaseSizeBeforeCreate = actionRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restActionMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(action))
            )
            .andExpect(status().isBadRequest());

        // Validate the Action in the database
        List<Action> actionList = actionRepository.findAll();
        assertThat(actionList).hasSize(databaseSizeBeforeCreate);

        // Validate the Action in Elasticsearch
        verify(mockActionSearchRepository, times(0)).save(action);
    }

    @Test
    @Transactional
    void getAllActions() throws Exception {
        // Initialize the database
        actionRepository.saveAndFlush(action);

        // Get all the actionList
        restActionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(action.getId().intValue())))
            .andExpect(jsonPath("$.[*].taskName").value(hasItem(DEFAULT_TASK_NAME)))
            .andExpect(jsonPath("$.[*].taskDescription").value(hasItem(DEFAULT_TASK_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].requiresPeriodicFollowup").value(hasItem(DEFAULT_REQUIRES_PERIODIC_FOLLOWUP.booleanValue())))
            .andExpect(jsonPath("$.[*].initialDeadline").value(hasItem(DEFAULT_INITIAL_DEADLINE.toString())))
            .andExpect(jsonPath("$.[*].updatedDeadline").value(hasItem(DEFAULT_UPDATED_DEADLINE.toString())))
            .andExpect(jsonPath("$.[*].doneDate").value(hasItem(DEFAULT_DONE_DATE.toString())))
            .andExpect(jsonPath("$.[*].verifiedDate").value(hasItem(DEFAULT_VERIFIED_DATE.toString())));
    }

    @Test
    @Transactional
    void getAction() throws Exception {
        // Initialize the database
        actionRepository.saveAndFlush(action);

        // Get the action
        restActionMockMvc
            .perform(get(ENTITY_API_URL_ID, action.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(action.getId().intValue()))
            .andExpect(jsonPath("$.taskName").value(DEFAULT_TASK_NAME))
            .andExpect(jsonPath("$.taskDescription").value(DEFAULT_TASK_DESCRIPTION))
            .andExpect(jsonPath("$.requiresPeriodicFollowup").value(DEFAULT_REQUIRES_PERIODIC_FOLLOWUP.booleanValue()))
            .andExpect(jsonPath("$.initialDeadline").value(DEFAULT_INITIAL_DEADLINE.toString()))
            .andExpect(jsonPath("$.updatedDeadline").value(DEFAULT_UPDATED_DEADLINE.toString()))
            .andExpect(jsonPath("$.doneDate").value(DEFAULT_DONE_DATE.toString()))
            .andExpect(jsonPath("$.verifiedDate").value(DEFAULT_VERIFIED_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingAction() throws Exception {
        // Get the action
        restActionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAction() throws Exception {
        // Initialize the database
        actionRepository.saveAndFlush(action);

        int databaseSizeBeforeUpdate = actionRepository.findAll().size();

        // Update the action
        Action updatedAction = actionRepository.findById(action.getId()).get();
        // Disconnect from session so that the updates on updatedAction are not directly saved in db
        em.detach(updatedAction);
        updatedAction
            .taskName(UPDATED_TASK_NAME)
            .taskDescription(UPDATED_TASK_DESCRIPTION)
            .requiresPeriodicFollowup(UPDATED_REQUIRES_PERIODIC_FOLLOWUP)
            .initialDeadline(UPDATED_INITIAL_DEADLINE)
            .updatedDeadline(UPDATED_UPDATED_DEADLINE)
            .doneDate(UPDATED_DONE_DATE)
            .verifiedDate(UPDATED_VERIFIED_DATE);

        restActionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAction.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedAction))
            )
            .andExpect(status().isOk());

        // Validate the Action in the database
        List<Action> actionList = actionRepository.findAll();
        assertThat(actionList).hasSize(databaseSizeBeforeUpdate);
        Action testAction = actionList.get(actionList.size() - 1);
        assertThat(testAction.getTaskName()).isEqualTo(UPDATED_TASK_NAME);
        assertThat(testAction.getTaskDescription()).isEqualTo(UPDATED_TASK_DESCRIPTION);
        assertThat(testAction.getRequiresPeriodicFollowup()).isEqualTo(UPDATED_REQUIRES_PERIODIC_FOLLOWUP);
        assertThat(testAction.getInitialDeadline()).isEqualTo(UPDATED_INITIAL_DEADLINE);
        assertThat(testAction.getUpdatedDeadline()).isEqualTo(UPDATED_UPDATED_DEADLINE);
        assertThat(testAction.getDoneDate()).isEqualTo(UPDATED_DONE_DATE);
        assertThat(testAction.getVerifiedDate()).isEqualTo(UPDATED_VERIFIED_DATE);

        // Validate the Action in Elasticsearch
        verify(mockActionSearchRepository).save(testAction);
    }

    @Test
    @Transactional
    void putNonExistingAction() throws Exception {
        int databaseSizeBeforeUpdate = actionRepository.findAll().size();
        action.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restActionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, action.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(action))
            )
            .andExpect(status().isBadRequest());

        // Validate the Action in the database
        List<Action> actionList = actionRepository.findAll();
        assertThat(actionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Action in Elasticsearch
        verify(mockActionSearchRepository, times(0)).save(action);
    }

    @Test
    @Transactional
    void putWithIdMismatchAction() throws Exception {
        int databaseSizeBeforeUpdate = actionRepository.findAll().size();
        action.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(action))
            )
            .andExpect(status().isBadRequest());

        // Validate the Action in the database
        List<Action> actionList = actionRepository.findAll();
        assertThat(actionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Action in Elasticsearch
        verify(mockActionSearchRepository, times(0)).save(action);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAction() throws Exception {
        int databaseSizeBeforeUpdate = actionRepository.findAll().size();
        action.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActionMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(action))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Action in the database
        List<Action> actionList = actionRepository.findAll();
        assertThat(actionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Action in Elasticsearch
        verify(mockActionSearchRepository, times(0)).save(action);
    }

    @Test
    @Transactional
    void partialUpdateActionWithPatch() throws Exception {
        // Initialize the database
        actionRepository.saveAndFlush(action);

        int databaseSizeBeforeUpdate = actionRepository.findAll().size();

        // Update the action using partial update
        Action partialUpdatedAction = new Action();
        partialUpdatedAction.setId(action.getId());

        partialUpdatedAction
            .taskName(UPDATED_TASK_NAME)
            .requiresPeriodicFollowup(UPDATED_REQUIRES_PERIODIC_FOLLOWUP)
            .initialDeadline(UPDATED_INITIAL_DEADLINE)
            .doneDate(UPDATED_DONE_DATE);

        restActionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAction.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAction))
            )
            .andExpect(status().isOk());

        // Validate the Action in the database
        List<Action> actionList = actionRepository.findAll();
        assertThat(actionList).hasSize(databaseSizeBeforeUpdate);
        Action testAction = actionList.get(actionList.size() - 1);
        assertThat(testAction.getTaskName()).isEqualTo(UPDATED_TASK_NAME);
        assertThat(testAction.getTaskDescription()).isEqualTo(DEFAULT_TASK_DESCRIPTION);
        assertThat(testAction.getRequiresPeriodicFollowup()).isEqualTo(UPDATED_REQUIRES_PERIODIC_FOLLOWUP);
        assertThat(testAction.getInitialDeadline()).isEqualTo(UPDATED_INITIAL_DEADLINE);
        assertThat(testAction.getUpdatedDeadline()).isEqualTo(DEFAULT_UPDATED_DEADLINE);
        assertThat(testAction.getDoneDate()).isEqualTo(UPDATED_DONE_DATE);
        assertThat(testAction.getVerifiedDate()).isEqualTo(DEFAULT_VERIFIED_DATE);
    }

    @Test
    @Transactional
    void fullUpdateActionWithPatch() throws Exception {
        // Initialize the database
        actionRepository.saveAndFlush(action);

        int databaseSizeBeforeUpdate = actionRepository.findAll().size();

        // Update the action using partial update
        Action partialUpdatedAction = new Action();
        partialUpdatedAction.setId(action.getId());

        partialUpdatedAction
            .taskName(UPDATED_TASK_NAME)
            .taskDescription(UPDATED_TASK_DESCRIPTION)
            .requiresPeriodicFollowup(UPDATED_REQUIRES_PERIODIC_FOLLOWUP)
            .initialDeadline(UPDATED_INITIAL_DEADLINE)
            .updatedDeadline(UPDATED_UPDATED_DEADLINE)
            .doneDate(UPDATED_DONE_DATE)
            .verifiedDate(UPDATED_VERIFIED_DATE);

        restActionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAction.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAction))
            )
            .andExpect(status().isOk());

        // Validate the Action in the database
        List<Action> actionList = actionRepository.findAll();
        assertThat(actionList).hasSize(databaseSizeBeforeUpdate);
        Action testAction = actionList.get(actionList.size() - 1);
        assertThat(testAction.getTaskName()).isEqualTo(UPDATED_TASK_NAME);
        assertThat(testAction.getTaskDescription()).isEqualTo(UPDATED_TASK_DESCRIPTION);
        assertThat(testAction.getRequiresPeriodicFollowup()).isEqualTo(UPDATED_REQUIRES_PERIODIC_FOLLOWUP);
        assertThat(testAction.getInitialDeadline()).isEqualTo(UPDATED_INITIAL_DEADLINE);
        assertThat(testAction.getUpdatedDeadline()).isEqualTo(UPDATED_UPDATED_DEADLINE);
        assertThat(testAction.getDoneDate()).isEqualTo(UPDATED_DONE_DATE);
        assertThat(testAction.getVerifiedDate()).isEqualTo(UPDATED_VERIFIED_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingAction() throws Exception {
        int databaseSizeBeforeUpdate = actionRepository.findAll().size();
        action.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restActionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, action.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(action))
            )
            .andExpect(status().isBadRequest());

        // Validate the Action in the database
        List<Action> actionList = actionRepository.findAll();
        assertThat(actionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Action in Elasticsearch
        verify(mockActionSearchRepository, times(0)).save(action);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAction() throws Exception {
        int databaseSizeBeforeUpdate = actionRepository.findAll().size();
        action.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(action))
            )
            .andExpect(status().isBadRequest());

        // Validate the Action in the database
        List<Action> actionList = actionRepository.findAll();
        assertThat(actionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Action in Elasticsearch
        verify(mockActionSearchRepository, times(0)).save(action);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAction() throws Exception {
        int databaseSizeBeforeUpdate = actionRepository.findAll().size();
        action.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActionMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(action))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Action in the database
        List<Action> actionList = actionRepository.findAll();
        assertThat(actionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Action in Elasticsearch
        verify(mockActionSearchRepository, times(0)).save(action);
    }

    @Test
    @Transactional
    void deleteAction() throws Exception {
        // Initialize the database
        actionRepository.saveAndFlush(action);

        int databaseSizeBeforeDelete = actionRepository.findAll().size();

        // Delete the action
        restActionMockMvc
            .perform(delete(ENTITY_API_URL_ID, action.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Action> actionList = actionRepository.findAll();
        assertThat(actionList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Action in Elasticsearch
        verify(mockActionSearchRepository, times(1)).deleteById(action.getId());
    }

    @Test
    @Transactional
    void searchAction() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        actionRepository.saveAndFlush(action);
        when(mockActionSearchRepository.search(queryStringQuery("id:" + action.getId()))).thenReturn(Collections.singletonList(action));

        // Search the action
        restActionMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + action.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(action.getId().intValue())))
            .andExpect(jsonPath("$.[*].taskName").value(hasItem(DEFAULT_TASK_NAME)))
            .andExpect(jsonPath("$.[*].taskDescription").value(hasItem(DEFAULT_TASK_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].requiresPeriodicFollowup").value(hasItem(DEFAULT_REQUIRES_PERIODIC_FOLLOWUP.booleanValue())))
            .andExpect(jsonPath("$.[*].initialDeadline").value(hasItem(DEFAULT_INITIAL_DEADLINE.toString())))
            .andExpect(jsonPath("$.[*].updatedDeadline").value(hasItem(DEFAULT_UPDATED_DEADLINE.toString())))
            .andExpect(jsonPath("$.[*].doneDate").value(hasItem(DEFAULT_DONE_DATE.toString())))
            .andExpect(jsonPath("$.[*].verifiedDate").value(hasItem(DEFAULT_VERIFIED_DATE.toString())));
    }
}

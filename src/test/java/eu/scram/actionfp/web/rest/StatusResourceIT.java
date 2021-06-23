package eu.scram.actionfp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import eu.scram.actionfp.IntegrationTest;
import eu.scram.actionfp.domain.Status;
import eu.scram.actionfp.repository.StatusRepository;
import eu.scram.actionfp.repository.search.StatusSearchRepository;
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
 * Integration tests for the {@link StatusResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class StatusResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/statuses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/statuses";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private StatusRepository statusRepository;

    /**
     * This repository is mocked in the eu.scram.actionfp.repository.search test package.
     *
     * @see eu.scram.actionfp.repository.search.StatusSearchRepositoryMockConfiguration
     */
    @Autowired
    private StatusSearchRepository mockStatusSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStatusMockMvc;

    private Status status;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Status createEntity(EntityManager em) {
        Status status = new Status().name(DEFAULT_NAME);
        return status;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Status createUpdatedEntity(EntityManager em) {
        Status status = new Status().name(UPDATED_NAME);
        return status;
    }

    @BeforeEach
    public void initTest() {
        status = createEntity(em);
    }

    @Test
    @Transactional
    void createStatus() throws Exception {
        int databaseSizeBeforeCreate = statusRepository.findAll().size();
        // Create the Status
        restStatusMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(status))
            )
            .andExpect(status().isCreated());

        // Validate the Status in the database
        List<Status> statusList = statusRepository.findAll();
        assertThat(statusList).hasSize(databaseSizeBeforeCreate + 1);
        Status testStatus = statusList.get(statusList.size() - 1);
        assertThat(testStatus.getName()).isEqualTo(DEFAULT_NAME);

        // Validate the Status in Elasticsearch
        verify(mockStatusSearchRepository, times(1)).save(testStatus);
    }

    @Test
    @Transactional
    void createStatusWithExistingId() throws Exception {
        // Create the Status with an existing ID
        status.setId(1L);

        int databaseSizeBeforeCreate = statusRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStatusMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(status))
            )
            .andExpect(status().isBadRequest());

        // Validate the Status in the database
        List<Status> statusList = statusRepository.findAll();
        assertThat(statusList).hasSize(databaseSizeBeforeCreate);

        // Validate the Status in Elasticsearch
        verify(mockStatusSearchRepository, times(0)).save(status);
    }

    @Test
    @Transactional
    void getAllStatuses() throws Exception {
        // Initialize the database
        statusRepository.saveAndFlush(status);

        // Get all the statusList
        restStatusMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(status.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getStatus() throws Exception {
        // Initialize the database
        statusRepository.saveAndFlush(status);

        // Get the status
        restStatusMockMvc
            .perform(get(ENTITY_API_URL_ID, status.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(status.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getNonExistingStatus() throws Exception {
        // Get the status
        restStatusMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewStatus() throws Exception {
        // Initialize the database
        statusRepository.saveAndFlush(status);

        int databaseSizeBeforeUpdate = statusRepository.findAll().size();

        // Update the status
        Status updatedStatus = statusRepository.findById(status.getId()).get();
        // Disconnect from session so that the updates on updatedStatus are not directly saved in db
        em.detach(updatedStatus);
        updatedStatus.name(UPDATED_NAME);

        restStatusMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedStatus.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedStatus))
            )
            .andExpect(status().isOk());

        // Validate the Status in the database
        List<Status> statusList = statusRepository.findAll();
        assertThat(statusList).hasSize(databaseSizeBeforeUpdate);
        Status testStatus = statusList.get(statusList.size() - 1);
        assertThat(testStatus.getName()).isEqualTo(UPDATED_NAME);

        // Validate the Status in Elasticsearch
        verify(mockStatusSearchRepository).save(testStatus);
    }

    @Test
    @Transactional
    void putNonExistingStatus() throws Exception {
        int databaseSizeBeforeUpdate = statusRepository.findAll().size();
        status.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStatusMockMvc
            .perform(
                put(ENTITY_API_URL_ID, status.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(status))
            )
            .andExpect(status().isBadRequest());

        // Validate the Status in the database
        List<Status> statusList = statusRepository.findAll();
        assertThat(statusList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Status in Elasticsearch
        verify(mockStatusSearchRepository, times(0)).save(status);
    }

    @Test
    @Transactional
    void putWithIdMismatchStatus() throws Exception {
        int databaseSizeBeforeUpdate = statusRepository.findAll().size();
        status.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatusMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(status))
            )
            .andExpect(status().isBadRequest());

        // Validate the Status in the database
        List<Status> statusList = statusRepository.findAll();
        assertThat(statusList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Status in Elasticsearch
        verify(mockStatusSearchRepository, times(0)).save(status);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStatus() throws Exception {
        int databaseSizeBeforeUpdate = statusRepository.findAll().size();
        status.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatusMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(status))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Status in the database
        List<Status> statusList = statusRepository.findAll();
        assertThat(statusList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Status in Elasticsearch
        verify(mockStatusSearchRepository, times(0)).save(status);
    }

    @Test
    @Transactional
    void partialUpdateStatusWithPatch() throws Exception {
        // Initialize the database
        statusRepository.saveAndFlush(status);

        int databaseSizeBeforeUpdate = statusRepository.findAll().size();

        // Update the status using partial update
        Status partialUpdatedStatus = new Status();
        partialUpdatedStatus.setId(status.getId());

        partialUpdatedStatus.name(UPDATED_NAME);

        restStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStatus.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStatus))
            )
            .andExpect(status().isOk());

        // Validate the Status in the database
        List<Status> statusList = statusRepository.findAll();
        assertThat(statusList).hasSize(databaseSizeBeforeUpdate);
        Status testStatus = statusList.get(statusList.size() - 1);
        assertThat(testStatus.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void fullUpdateStatusWithPatch() throws Exception {
        // Initialize the database
        statusRepository.saveAndFlush(status);

        int databaseSizeBeforeUpdate = statusRepository.findAll().size();

        // Update the status using partial update
        Status partialUpdatedStatus = new Status();
        partialUpdatedStatus.setId(status.getId());

        partialUpdatedStatus.name(UPDATED_NAME);

        restStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStatus.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStatus))
            )
            .andExpect(status().isOk());

        // Validate the Status in the database
        List<Status> statusList = statusRepository.findAll();
        assertThat(statusList).hasSize(databaseSizeBeforeUpdate);
        Status testStatus = statusList.get(statusList.size() - 1);
        assertThat(testStatus.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingStatus() throws Exception {
        int databaseSizeBeforeUpdate = statusRepository.findAll().size();
        status.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, status.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(status))
            )
            .andExpect(status().isBadRequest());

        // Validate the Status in the database
        List<Status> statusList = statusRepository.findAll();
        assertThat(statusList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Status in Elasticsearch
        verify(mockStatusSearchRepository, times(0)).save(status);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStatus() throws Exception {
        int databaseSizeBeforeUpdate = statusRepository.findAll().size();
        status.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(status))
            )
            .andExpect(status().isBadRequest());

        // Validate the Status in the database
        List<Status> statusList = statusRepository.findAll();
        assertThat(statusList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Status in Elasticsearch
        verify(mockStatusSearchRepository, times(0)).save(status);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStatus() throws Exception {
        int databaseSizeBeforeUpdate = statusRepository.findAll().size();
        status.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatusMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(status))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Status in the database
        List<Status> statusList = statusRepository.findAll();
        assertThat(statusList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Status in Elasticsearch
        verify(mockStatusSearchRepository, times(0)).save(status);
    }

    @Test
    @Transactional
    void deleteStatus() throws Exception {
        // Initialize the database
        statusRepository.saveAndFlush(status);

        int databaseSizeBeforeDelete = statusRepository.findAll().size();

        // Delete the status
        restStatusMockMvc
            .perform(delete(ENTITY_API_URL_ID, status.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Status> statusList = statusRepository.findAll();
        assertThat(statusList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Status in Elasticsearch
        verify(mockStatusSearchRepository, times(1)).deleteById(status.getId());
    }

    @Test
    @Transactional
    void searchStatus() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        statusRepository.saveAndFlush(status);
        when(mockStatusSearchRepository.search(queryStringQuery("id:" + status.getId()))).thenReturn(Collections.singletonList(status));

        // Search the status
        restStatusMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + status.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(status.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }
}

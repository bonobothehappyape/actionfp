package eu.scram.actionfp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import eu.scram.actionfp.IntegrationTest;
import eu.scram.actionfp.domain.AuditRecomm;
import eu.scram.actionfp.repository.AuditRecommRepository;
import eu.scram.actionfp.repository.search.AuditRecommSearchRepository;
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
 * Integration tests for the {@link AuditRecommResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AuditRecommResourceIT {

    private static final String DEFAULT_RECOMM_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_RECOMM_NUMBER = "BBBBBBBBBB";

    private static final Long DEFAULT_PRIORITY = 1L;
    private static final Long UPDATED_PRIORITY = 2L;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/audit-recomms";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/audit-recomms";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AuditRecommRepository auditRecommRepository;

    /**
     * This repository is mocked in the eu.scram.actionfp.repository.search test package.
     *
     * @see eu.scram.actionfp.repository.search.AuditRecommSearchRepositoryMockConfiguration
     */
    @Autowired
    private AuditRecommSearchRepository mockAuditRecommSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAuditRecommMockMvc;

    private AuditRecomm auditRecomm;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AuditRecomm createEntity(EntityManager em) {
        AuditRecomm auditRecomm = new AuditRecomm()
            .recommNumber(DEFAULT_RECOMM_NUMBER)
            .priority(DEFAULT_PRIORITY)
            .description(DEFAULT_DESCRIPTION);
        return auditRecomm;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AuditRecomm createUpdatedEntity(EntityManager em) {
        AuditRecomm auditRecomm = new AuditRecomm()
            .recommNumber(UPDATED_RECOMM_NUMBER)
            .priority(UPDATED_PRIORITY)
            .description(UPDATED_DESCRIPTION);
        return auditRecomm;
    }

    @BeforeEach
    public void initTest() {
        auditRecomm = createEntity(em);
    }

    @Test
    @Transactional
    void createAuditRecomm() throws Exception {
        int databaseSizeBeforeCreate = auditRecommRepository.findAll().size();
        // Create the AuditRecomm
        restAuditRecommMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(auditRecomm))
            )
            .andExpect(status().isCreated());

        // Validate the AuditRecomm in the database
        List<AuditRecomm> auditRecommList = auditRecommRepository.findAll();
        assertThat(auditRecommList).hasSize(databaseSizeBeforeCreate + 1);
        AuditRecomm testAuditRecomm = auditRecommList.get(auditRecommList.size() - 1);
        assertThat(testAuditRecomm.getRecommNumber()).isEqualTo(DEFAULT_RECOMM_NUMBER);
        assertThat(testAuditRecomm.getPriority()).isEqualTo(DEFAULT_PRIORITY);
        assertThat(testAuditRecomm.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the AuditRecomm in Elasticsearch
        verify(mockAuditRecommSearchRepository, times(1)).save(testAuditRecomm);
    }

    @Test
    @Transactional
    void createAuditRecommWithExistingId() throws Exception {
        // Create the AuditRecomm with an existing ID
        auditRecomm.setId(1L);

        int databaseSizeBeforeCreate = auditRecommRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAuditRecommMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(auditRecomm))
            )
            .andExpect(status().isBadRequest());

        // Validate the AuditRecomm in the database
        List<AuditRecomm> auditRecommList = auditRecommRepository.findAll();
        assertThat(auditRecommList).hasSize(databaseSizeBeforeCreate);

        // Validate the AuditRecomm in Elasticsearch
        verify(mockAuditRecommSearchRepository, times(0)).save(auditRecomm);
    }

    @Test
    @Transactional
    void getAllAuditRecomms() throws Exception {
        // Initialize the database
        auditRecommRepository.saveAndFlush(auditRecomm);

        // Get all the auditRecommList
        restAuditRecommMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(auditRecomm.getId().intValue())))
            .andExpect(jsonPath("$.[*].recommNumber").value(hasItem(DEFAULT_RECOMM_NUMBER)))
            .andExpect(jsonPath("$.[*].priority").value(hasItem(DEFAULT_PRIORITY.intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getAuditRecomm() throws Exception {
        // Initialize the database
        auditRecommRepository.saveAndFlush(auditRecomm);

        // Get the auditRecomm
        restAuditRecommMockMvc
            .perform(get(ENTITY_API_URL_ID, auditRecomm.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(auditRecomm.getId().intValue()))
            .andExpect(jsonPath("$.recommNumber").value(DEFAULT_RECOMM_NUMBER))
            .andExpect(jsonPath("$.priority").value(DEFAULT_PRIORITY.intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingAuditRecomm() throws Exception {
        // Get the auditRecomm
        restAuditRecommMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAuditRecomm() throws Exception {
        // Initialize the database
        auditRecommRepository.saveAndFlush(auditRecomm);

        int databaseSizeBeforeUpdate = auditRecommRepository.findAll().size();

        // Update the auditRecomm
        AuditRecomm updatedAuditRecomm = auditRecommRepository.findById(auditRecomm.getId()).get();
        // Disconnect from session so that the updates on updatedAuditRecomm are not directly saved in db
        em.detach(updatedAuditRecomm);
        updatedAuditRecomm.recommNumber(UPDATED_RECOMM_NUMBER).priority(UPDATED_PRIORITY).description(UPDATED_DESCRIPTION);

        restAuditRecommMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAuditRecomm.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedAuditRecomm))
            )
            .andExpect(status().isOk());

        // Validate the AuditRecomm in the database
        List<AuditRecomm> auditRecommList = auditRecommRepository.findAll();
        assertThat(auditRecommList).hasSize(databaseSizeBeforeUpdate);
        AuditRecomm testAuditRecomm = auditRecommList.get(auditRecommList.size() - 1);
        assertThat(testAuditRecomm.getRecommNumber()).isEqualTo(UPDATED_RECOMM_NUMBER);
        assertThat(testAuditRecomm.getPriority()).isEqualTo(UPDATED_PRIORITY);
        assertThat(testAuditRecomm.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the AuditRecomm in Elasticsearch
        verify(mockAuditRecommSearchRepository).save(testAuditRecomm);
    }

    @Test
    @Transactional
    void putNonExistingAuditRecomm() throws Exception {
        int databaseSizeBeforeUpdate = auditRecommRepository.findAll().size();
        auditRecomm.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAuditRecommMockMvc
            .perform(
                put(ENTITY_API_URL_ID, auditRecomm.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(auditRecomm))
            )
            .andExpect(status().isBadRequest());

        // Validate the AuditRecomm in the database
        List<AuditRecomm> auditRecommList = auditRecommRepository.findAll();
        assertThat(auditRecommList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AuditRecomm in Elasticsearch
        verify(mockAuditRecommSearchRepository, times(0)).save(auditRecomm);
    }

    @Test
    @Transactional
    void putWithIdMismatchAuditRecomm() throws Exception {
        int databaseSizeBeforeUpdate = auditRecommRepository.findAll().size();
        auditRecomm.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuditRecommMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(auditRecomm))
            )
            .andExpect(status().isBadRequest());

        // Validate the AuditRecomm in the database
        List<AuditRecomm> auditRecommList = auditRecommRepository.findAll();
        assertThat(auditRecommList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AuditRecomm in Elasticsearch
        verify(mockAuditRecommSearchRepository, times(0)).save(auditRecomm);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAuditRecomm() throws Exception {
        int databaseSizeBeforeUpdate = auditRecommRepository.findAll().size();
        auditRecomm.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuditRecommMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(auditRecomm))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AuditRecomm in the database
        List<AuditRecomm> auditRecommList = auditRecommRepository.findAll();
        assertThat(auditRecommList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AuditRecomm in Elasticsearch
        verify(mockAuditRecommSearchRepository, times(0)).save(auditRecomm);
    }

    @Test
    @Transactional
    void partialUpdateAuditRecommWithPatch() throws Exception {
        // Initialize the database
        auditRecommRepository.saveAndFlush(auditRecomm);

        int databaseSizeBeforeUpdate = auditRecommRepository.findAll().size();

        // Update the auditRecomm using partial update
        AuditRecomm partialUpdatedAuditRecomm = new AuditRecomm();
        partialUpdatedAuditRecomm.setId(auditRecomm.getId());

        partialUpdatedAuditRecomm.recommNumber(UPDATED_RECOMM_NUMBER);

        restAuditRecommMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAuditRecomm.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAuditRecomm))
            )
            .andExpect(status().isOk());

        // Validate the AuditRecomm in the database
        List<AuditRecomm> auditRecommList = auditRecommRepository.findAll();
        assertThat(auditRecommList).hasSize(databaseSizeBeforeUpdate);
        AuditRecomm testAuditRecomm = auditRecommList.get(auditRecommList.size() - 1);
        assertThat(testAuditRecomm.getRecommNumber()).isEqualTo(UPDATED_RECOMM_NUMBER);
        assertThat(testAuditRecomm.getPriority()).isEqualTo(DEFAULT_PRIORITY);
        assertThat(testAuditRecomm.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateAuditRecommWithPatch() throws Exception {
        // Initialize the database
        auditRecommRepository.saveAndFlush(auditRecomm);

        int databaseSizeBeforeUpdate = auditRecommRepository.findAll().size();

        // Update the auditRecomm using partial update
        AuditRecomm partialUpdatedAuditRecomm = new AuditRecomm();
        partialUpdatedAuditRecomm.setId(auditRecomm.getId());

        partialUpdatedAuditRecomm.recommNumber(UPDATED_RECOMM_NUMBER).priority(UPDATED_PRIORITY).description(UPDATED_DESCRIPTION);

        restAuditRecommMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAuditRecomm.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAuditRecomm))
            )
            .andExpect(status().isOk());

        // Validate the AuditRecomm in the database
        List<AuditRecomm> auditRecommList = auditRecommRepository.findAll();
        assertThat(auditRecommList).hasSize(databaseSizeBeforeUpdate);
        AuditRecomm testAuditRecomm = auditRecommList.get(auditRecommList.size() - 1);
        assertThat(testAuditRecomm.getRecommNumber()).isEqualTo(UPDATED_RECOMM_NUMBER);
        assertThat(testAuditRecomm.getPriority()).isEqualTo(UPDATED_PRIORITY);
        assertThat(testAuditRecomm.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingAuditRecomm() throws Exception {
        int databaseSizeBeforeUpdate = auditRecommRepository.findAll().size();
        auditRecomm.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAuditRecommMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, auditRecomm.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(auditRecomm))
            )
            .andExpect(status().isBadRequest());

        // Validate the AuditRecomm in the database
        List<AuditRecomm> auditRecommList = auditRecommRepository.findAll();
        assertThat(auditRecommList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AuditRecomm in Elasticsearch
        verify(mockAuditRecommSearchRepository, times(0)).save(auditRecomm);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAuditRecomm() throws Exception {
        int databaseSizeBeforeUpdate = auditRecommRepository.findAll().size();
        auditRecomm.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuditRecommMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(auditRecomm))
            )
            .andExpect(status().isBadRequest());

        // Validate the AuditRecomm in the database
        List<AuditRecomm> auditRecommList = auditRecommRepository.findAll();
        assertThat(auditRecommList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AuditRecomm in Elasticsearch
        verify(mockAuditRecommSearchRepository, times(0)).save(auditRecomm);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAuditRecomm() throws Exception {
        int databaseSizeBeforeUpdate = auditRecommRepository.findAll().size();
        auditRecomm.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuditRecommMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(auditRecomm))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AuditRecomm in the database
        List<AuditRecomm> auditRecommList = auditRecommRepository.findAll();
        assertThat(auditRecommList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AuditRecomm in Elasticsearch
        verify(mockAuditRecommSearchRepository, times(0)).save(auditRecomm);
    }

    @Test
    @Transactional
    void deleteAuditRecomm() throws Exception {
        // Initialize the database
        auditRecommRepository.saveAndFlush(auditRecomm);

        int databaseSizeBeforeDelete = auditRecommRepository.findAll().size();

        // Delete the auditRecomm
        restAuditRecommMockMvc
            .perform(delete(ENTITY_API_URL_ID, auditRecomm.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<AuditRecomm> auditRecommList = auditRecommRepository.findAll();
        assertThat(auditRecommList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the AuditRecomm in Elasticsearch
        verify(mockAuditRecommSearchRepository, times(1)).deleteById(auditRecomm.getId());
    }

    @Test
    @Transactional
    void searchAuditRecomm() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        auditRecommRepository.saveAndFlush(auditRecomm);
        when(mockAuditRecommSearchRepository.search(queryStringQuery("id:" + auditRecomm.getId())))
            .thenReturn(Collections.singletonList(auditRecomm));

        // Search the auditRecomm
        restAuditRecommMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + auditRecomm.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(auditRecomm.getId().intValue())))
            .andExpect(jsonPath("$.[*].recommNumber").value(hasItem(DEFAULT_RECOMM_NUMBER)))
            .andExpect(jsonPath("$.[*].priority").value(hasItem(DEFAULT_PRIORITY.intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }
}

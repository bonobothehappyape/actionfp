package eu.scram.actionfp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import eu.scram.actionfp.IntegrationTest;
import eu.scram.actionfp.domain.AuditSubRecomm;
import eu.scram.actionfp.repository.AuditSubRecommRepository;
import eu.scram.actionfp.repository.search.AuditSubRecommSearchRepository;
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
 * Integration tests for the {@link AuditSubRecommResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AuditSubRecommResourceIT {

    private static final String DEFAULT_SUB_RECOMM_NUM = "AAAAAAAAAA";
    private static final String UPDATED_SUB_RECOMM_NUM = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/audit-sub-recomms";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/audit-sub-recomms";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AuditSubRecommRepository auditSubRecommRepository;

    /**
     * This repository is mocked in the eu.scram.actionfp.repository.search test package.
     *
     * @see eu.scram.actionfp.repository.search.AuditSubRecommSearchRepositoryMockConfiguration
     */
    @Autowired
    private AuditSubRecommSearchRepository mockAuditSubRecommSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAuditSubRecommMockMvc;

    private AuditSubRecomm auditSubRecomm;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AuditSubRecomm createEntity(EntityManager em) {
        AuditSubRecomm auditSubRecomm = new AuditSubRecomm().subRecommNum(DEFAULT_SUB_RECOMM_NUM).description(DEFAULT_DESCRIPTION);
        return auditSubRecomm;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AuditSubRecomm createUpdatedEntity(EntityManager em) {
        AuditSubRecomm auditSubRecomm = new AuditSubRecomm().subRecommNum(UPDATED_SUB_RECOMM_NUM).description(UPDATED_DESCRIPTION);
        return auditSubRecomm;
    }

    @BeforeEach
    public void initTest() {
        auditSubRecomm = createEntity(em);
    }

    @Test
    @Transactional
    void createAuditSubRecomm() throws Exception {
        int databaseSizeBeforeCreate = auditSubRecommRepository.findAll().size();
        // Create the AuditSubRecomm
        restAuditSubRecommMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(auditSubRecomm))
            )
            .andExpect(status().isCreated());

        // Validate the AuditSubRecomm in the database
        List<AuditSubRecomm> auditSubRecommList = auditSubRecommRepository.findAll();
        assertThat(auditSubRecommList).hasSize(databaseSizeBeforeCreate + 1);
        AuditSubRecomm testAuditSubRecomm = auditSubRecommList.get(auditSubRecommList.size() - 1);
        assertThat(testAuditSubRecomm.getSubRecommNum()).isEqualTo(DEFAULT_SUB_RECOMM_NUM);
        assertThat(testAuditSubRecomm.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the AuditSubRecomm in Elasticsearch
        verify(mockAuditSubRecommSearchRepository, times(1)).save(testAuditSubRecomm);
    }

    @Test
    @Transactional
    void createAuditSubRecommWithExistingId() throws Exception {
        // Create the AuditSubRecomm with an existing ID
        auditSubRecomm.setId(1L);

        int databaseSizeBeforeCreate = auditSubRecommRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAuditSubRecommMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(auditSubRecomm))
            )
            .andExpect(status().isBadRequest());

        // Validate the AuditSubRecomm in the database
        List<AuditSubRecomm> auditSubRecommList = auditSubRecommRepository.findAll();
        assertThat(auditSubRecommList).hasSize(databaseSizeBeforeCreate);

        // Validate the AuditSubRecomm in Elasticsearch
        verify(mockAuditSubRecommSearchRepository, times(0)).save(auditSubRecomm);
    }

    @Test
    @Transactional
    void getAllAuditSubRecomms() throws Exception {
        // Initialize the database
        auditSubRecommRepository.saveAndFlush(auditSubRecomm);

        // Get all the auditSubRecommList
        restAuditSubRecommMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(auditSubRecomm.getId().intValue())))
            .andExpect(jsonPath("$.[*].subRecommNum").value(hasItem(DEFAULT_SUB_RECOMM_NUM)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getAuditSubRecomm() throws Exception {
        // Initialize the database
        auditSubRecommRepository.saveAndFlush(auditSubRecomm);

        // Get the auditSubRecomm
        restAuditSubRecommMockMvc
            .perform(get(ENTITY_API_URL_ID, auditSubRecomm.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(auditSubRecomm.getId().intValue()))
            .andExpect(jsonPath("$.subRecommNum").value(DEFAULT_SUB_RECOMM_NUM))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingAuditSubRecomm() throws Exception {
        // Get the auditSubRecomm
        restAuditSubRecommMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAuditSubRecomm() throws Exception {
        // Initialize the database
        auditSubRecommRepository.saveAndFlush(auditSubRecomm);

        int databaseSizeBeforeUpdate = auditSubRecommRepository.findAll().size();

        // Update the auditSubRecomm
        AuditSubRecomm updatedAuditSubRecomm = auditSubRecommRepository.findById(auditSubRecomm.getId()).get();
        // Disconnect from session so that the updates on updatedAuditSubRecomm are not directly saved in db
        em.detach(updatedAuditSubRecomm);
        updatedAuditSubRecomm.subRecommNum(UPDATED_SUB_RECOMM_NUM).description(UPDATED_DESCRIPTION);

        restAuditSubRecommMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAuditSubRecomm.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedAuditSubRecomm))
            )
            .andExpect(status().isOk());

        // Validate the AuditSubRecomm in the database
        List<AuditSubRecomm> auditSubRecommList = auditSubRecommRepository.findAll();
        assertThat(auditSubRecommList).hasSize(databaseSizeBeforeUpdate);
        AuditSubRecomm testAuditSubRecomm = auditSubRecommList.get(auditSubRecommList.size() - 1);
        assertThat(testAuditSubRecomm.getSubRecommNum()).isEqualTo(UPDATED_SUB_RECOMM_NUM);
        assertThat(testAuditSubRecomm.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the AuditSubRecomm in Elasticsearch
        verify(mockAuditSubRecommSearchRepository).save(testAuditSubRecomm);
    }

    @Test
    @Transactional
    void putNonExistingAuditSubRecomm() throws Exception {
        int databaseSizeBeforeUpdate = auditSubRecommRepository.findAll().size();
        auditSubRecomm.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAuditSubRecommMockMvc
            .perform(
                put(ENTITY_API_URL_ID, auditSubRecomm.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(auditSubRecomm))
            )
            .andExpect(status().isBadRequest());

        // Validate the AuditSubRecomm in the database
        List<AuditSubRecomm> auditSubRecommList = auditSubRecommRepository.findAll();
        assertThat(auditSubRecommList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AuditSubRecomm in Elasticsearch
        verify(mockAuditSubRecommSearchRepository, times(0)).save(auditSubRecomm);
    }

    @Test
    @Transactional
    void putWithIdMismatchAuditSubRecomm() throws Exception {
        int databaseSizeBeforeUpdate = auditSubRecommRepository.findAll().size();
        auditSubRecomm.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuditSubRecommMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(auditSubRecomm))
            )
            .andExpect(status().isBadRequest());

        // Validate the AuditSubRecomm in the database
        List<AuditSubRecomm> auditSubRecommList = auditSubRecommRepository.findAll();
        assertThat(auditSubRecommList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AuditSubRecomm in Elasticsearch
        verify(mockAuditSubRecommSearchRepository, times(0)).save(auditSubRecomm);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAuditSubRecomm() throws Exception {
        int databaseSizeBeforeUpdate = auditSubRecommRepository.findAll().size();
        auditSubRecomm.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuditSubRecommMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(auditSubRecomm))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AuditSubRecomm in the database
        List<AuditSubRecomm> auditSubRecommList = auditSubRecommRepository.findAll();
        assertThat(auditSubRecommList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AuditSubRecomm in Elasticsearch
        verify(mockAuditSubRecommSearchRepository, times(0)).save(auditSubRecomm);
    }

    @Test
    @Transactional
    void partialUpdateAuditSubRecommWithPatch() throws Exception {
        // Initialize the database
        auditSubRecommRepository.saveAndFlush(auditSubRecomm);

        int databaseSizeBeforeUpdate = auditSubRecommRepository.findAll().size();

        // Update the auditSubRecomm using partial update
        AuditSubRecomm partialUpdatedAuditSubRecomm = new AuditSubRecomm();
        partialUpdatedAuditSubRecomm.setId(auditSubRecomm.getId());

        partialUpdatedAuditSubRecomm.subRecommNum(UPDATED_SUB_RECOMM_NUM).description(UPDATED_DESCRIPTION);

        restAuditSubRecommMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAuditSubRecomm.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAuditSubRecomm))
            )
            .andExpect(status().isOk());

        // Validate the AuditSubRecomm in the database
        List<AuditSubRecomm> auditSubRecommList = auditSubRecommRepository.findAll();
        assertThat(auditSubRecommList).hasSize(databaseSizeBeforeUpdate);
        AuditSubRecomm testAuditSubRecomm = auditSubRecommList.get(auditSubRecommList.size() - 1);
        assertThat(testAuditSubRecomm.getSubRecommNum()).isEqualTo(UPDATED_SUB_RECOMM_NUM);
        assertThat(testAuditSubRecomm.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateAuditSubRecommWithPatch() throws Exception {
        // Initialize the database
        auditSubRecommRepository.saveAndFlush(auditSubRecomm);

        int databaseSizeBeforeUpdate = auditSubRecommRepository.findAll().size();

        // Update the auditSubRecomm using partial update
        AuditSubRecomm partialUpdatedAuditSubRecomm = new AuditSubRecomm();
        partialUpdatedAuditSubRecomm.setId(auditSubRecomm.getId());

        partialUpdatedAuditSubRecomm.subRecommNum(UPDATED_SUB_RECOMM_NUM).description(UPDATED_DESCRIPTION);

        restAuditSubRecommMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAuditSubRecomm.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAuditSubRecomm))
            )
            .andExpect(status().isOk());

        // Validate the AuditSubRecomm in the database
        List<AuditSubRecomm> auditSubRecommList = auditSubRecommRepository.findAll();
        assertThat(auditSubRecommList).hasSize(databaseSizeBeforeUpdate);
        AuditSubRecomm testAuditSubRecomm = auditSubRecommList.get(auditSubRecommList.size() - 1);
        assertThat(testAuditSubRecomm.getSubRecommNum()).isEqualTo(UPDATED_SUB_RECOMM_NUM);
        assertThat(testAuditSubRecomm.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingAuditSubRecomm() throws Exception {
        int databaseSizeBeforeUpdate = auditSubRecommRepository.findAll().size();
        auditSubRecomm.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAuditSubRecommMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, auditSubRecomm.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(auditSubRecomm))
            )
            .andExpect(status().isBadRequest());

        // Validate the AuditSubRecomm in the database
        List<AuditSubRecomm> auditSubRecommList = auditSubRecommRepository.findAll();
        assertThat(auditSubRecommList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AuditSubRecomm in Elasticsearch
        verify(mockAuditSubRecommSearchRepository, times(0)).save(auditSubRecomm);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAuditSubRecomm() throws Exception {
        int databaseSizeBeforeUpdate = auditSubRecommRepository.findAll().size();
        auditSubRecomm.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuditSubRecommMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(auditSubRecomm))
            )
            .andExpect(status().isBadRequest());

        // Validate the AuditSubRecomm in the database
        List<AuditSubRecomm> auditSubRecommList = auditSubRecommRepository.findAll();
        assertThat(auditSubRecommList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AuditSubRecomm in Elasticsearch
        verify(mockAuditSubRecommSearchRepository, times(0)).save(auditSubRecomm);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAuditSubRecomm() throws Exception {
        int databaseSizeBeforeUpdate = auditSubRecommRepository.findAll().size();
        auditSubRecomm.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuditSubRecommMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(auditSubRecomm))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AuditSubRecomm in the database
        List<AuditSubRecomm> auditSubRecommList = auditSubRecommRepository.findAll();
        assertThat(auditSubRecommList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AuditSubRecomm in Elasticsearch
        verify(mockAuditSubRecommSearchRepository, times(0)).save(auditSubRecomm);
    }

    @Test
    @Transactional
    void deleteAuditSubRecomm() throws Exception {
        // Initialize the database
        auditSubRecommRepository.saveAndFlush(auditSubRecomm);

        int databaseSizeBeforeDelete = auditSubRecommRepository.findAll().size();

        // Delete the auditSubRecomm
        restAuditSubRecommMockMvc
            .perform(delete(ENTITY_API_URL_ID, auditSubRecomm.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<AuditSubRecomm> auditSubRecommList = auditSubRecommRepository.findAll();
        assertThat(auditSubRecommList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the AuditSubRecomm in Elasticsearch
        verify(mockAuditSubRecommSearchRepository, times(1)).deleteById(auditSubRecomm.getId());
    }

    @Test
    @Transactional
    void searchAuditSubRecomm() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        auditSubRecommRepository.saveAndFlush(auditSubRecomm);
        when(mockAuditSubRecommSearchRepository.search(queryStringQuery("id:" + auditSubRecomm.getId())))
            .thenReturn(Collections.singletonList(auditSubRecomm));

        // Search the auditSubRecomm
        restAuditSubRecommMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + auditSubRecomm.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(auditSubRecomm.getId().intValue())))
            .andExpect(jsonPath("$.[*].subRecommNum").value(hasItem(DEFAULT_SUB_RECOMM_NUM)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }
}

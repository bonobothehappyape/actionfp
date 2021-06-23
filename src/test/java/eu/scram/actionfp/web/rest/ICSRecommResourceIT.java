package eu.scram.actionfp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import eu.scram.actionfp.IntegrationTest;
import eu.scram.actionfp.domain.ICSRecomm;
import eu.scram.actionfp.repository.ICSRecommRepository;
import eu.scram.actionfp.repository.search.ICSRecommSearchRepository;
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
 * Integration tests for the {@link ICSRecommResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ICSRecommResourceIT {

    private static final Integer DEFAULT_YEAR = 1;
    private static final Integer UPDATED_YEAR = 2;

    private static final String DEFAULT_ICS_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_ICS_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_ICS_DESCR = "AAAAAAAAAA";
    private static final String UPDATED_ICS_DESCR = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/ics-recomms";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/ics-recomms";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ICSRecommRepository iCSRecommRepository;

    /**
     * This repository is mocked in the eu.scram.actionfp.repository.search test package.
     *
     * @see eu.scram.actionfp.repository.search.ICSRecommSearchRepositoryMockConfiguration
     */
    @Autowired
    private ICSRecommSearchRepository mockICSRecommSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restICSRecommMockMvc;

    private ICSRecomm iCSRecomm;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ICSRecomm createEntity(EntityManager em) {
        ICSRecomm iCSRecomm = new ICSRecomm()
            .year(DEFAULT_YEAR)
            .icsNumber(DEFAULT_ICS_NUMBER)
            .icsDescr(DEFAULT_ICS_DESCR)
            .title(DEFAULT_TITLE);
        return iCSRecomm;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ICSRecomm createUpdatedEntity(EntityManager em) {
        ICSRecomm iCSRecomm = new ICSRecomm()
            .year(UPDATED_YEAR)
            .icsNumber(UPDATED_ICS_NUMBER)
            .icsDescr(UPDATED_ICS_DESCR)
            .title(UPDATED_TITLE);
        return iCSRecomm;
    }

    @BeforeEach
    public void initTest() {
        iCSRecomm = createEntity(em);
    }

    @Test
    @Transactional
    void createICSRecomm() throws Exception {
        int databaseSizeBeforeCreate = iCSRecommRepository.findAll().size();
        // Create the ICSRecomm
        restICSRecommMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(iCSRecomm))
            )
            .andExpect(status().isCreated());

        // Validate the ICSRecomm in the database
        List<ICSRecomm> iCSRecommList = iCSRecommRepository.findAll();
        assertThat(iCSRecommList).hasSize(databaseSizeBeforeCreate + 1);
        ICSRecomm testICSRecomm = iCSRecommList.get(iCSRecommList.size() - 1);
        assertThat(testICSRecomm.getYear()).isEqualTo(DEFAULT_YEAR);
        assertThat(testICSRecomm.getIcsNumber()).isEqualTo(DEFAULT_ICS_NUMBER);
        assertThat(testICSRecomm.getIcsDescr()).isEqualTo(DEFAULT_ICS_DESCR);
        assertThat(testICSRecomm.getTitle()).isEqualTo(DEFAULT_TITLE);

        // Validate the ICSRecomm in Elasticsearch
        verify(mockICSRecommSearchRepository, times(1)).save(testICSRecomm);
    }

    @Test
    @Transactional
    void createICSRecommWithExistingId() throws Exception {
        // Create the ICSRecomm with an existing ID
        iCSRecomm.setId(1L);

        int databaseSizeBeforeCreate = iCSRecommRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restICSRecommMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(iCSRecomm))
            )
            .andExpect(status().isBadRequest());

        // Validate the ICSRecomm in the database
        List<ICSRecomm> iCSRecommList = iCSRecommRepository.findAll();
        assertThat(iCSRecommList).hasSize(databaseSizeBeforeCreate);

        // Validate the ICSRecomm in Elasticsearch
        verify(mockICSRecommSearchRepository, times(0)).save(iCSRecomm);
    }

    @Test
    @Transactional
    void getAllICSRecomms() throws Exception {
        // Initialize the database
        iCSRecommRepository.saveAndFlush(iCSRecomm);

        // Get all the iCSRecommList
        restICSRecommMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(iCSRecomm.getId().intValue())))
            .andExpect(jsonPath("$.[*].year").value(hasItem(DEFAULT_YEAR)))
            .andExpect(jsonPath("$.[*].icsNumber").value(hasItem(DEFAULT_ICS_NUMBER)))
            .andExpect(jsonPath("$.[*].icsDescr").value(hasItem(DEFAULT_ICS_DESCR)))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)));
    }

    @Test
    @Transactional
    void getICSRecomm() throws Exception {
        // Initialize the database
        iCSRecommRepository.saveAndFlush(iCSRecomm);

        // Get the iCSRecomm
        restICSRecommMockMvc
            .perform(get(ENTITY_API_URL_ID, iCSRecomm.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(iCSRecomm.getId().intValue()))
            .andExpect(jsonPath("$.year").value(DEFAULT_YEAR))
            .andExpect(jsonPath("$.icsNumber").value(DEFAULT_ICS_NUMBER))
            .andExpect(jsonPath("$.icsDescr").value(DEFAULT_ICS_DESCR))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE));
    }

    @Test
    @Transactional
    void getNonExistingICSRecomm() throws Exception {
        // Get the iCSRecomm
        restICSRecommMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewICSRecomm() throws Exception {
        // Initialize the database
        iCSRecommRepository.saveAndFlush(iCSRecomm);

        int databaseSizeBeforeUpdate = iCSRecommRepository.findAll().size();

        // Update the iCSRecomm
        ICSRecomm updatedICSRecomm = iCSRecommRepository.findById(iCSRecomm.getId()).get();
        // Disconnect from session so that the updates on updatedICSRecomm are not directly saved in db
        em.detach(updatedICSRecomm);
        updatedICSRecomm.year(UPDATED_YEAR).icsNumber(UPDATED_ICS_NUMBER).icsDescr(UPDATED_ICS_DESCR).title(UPDATED_TITLE);

        restICSRecommMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedICSRecomm.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedICSRecomm))
            )
            .andExpect(status().isOk());

        // Validate the ICSRecomm in the database
        List<ICSRecomm> iCSRecommList = iCSRecommRepository.findAll();
        assertThat(iCSRecommList).hasSize(databaseSizeBeforeUpdate);
        ICSRecomm testICSRecomm = iCSRecommList.get(iCSRecommList.size() - 1);
        assertThat(testICSRecomm.getYear()).isEqualTo(UPDATED_YEAR);
        assertThat(testICSRecomm.getIcsNumber()).isEqualTo(UPDATED_ICS_NUMBER);
        assertThat(testICSRecomm.getIcsDescr()).isEqualTo(UPDATED_ICS_DESCR);
        assertThat(testICSRecomm.getTitle()).isEqualTo(UPDATED_TITLE);

        // Validate the ICSRecomm in Elasticsearch
        verify(mockICSRecommSearchRepository).save(testICSRecomm);
    }

    @Test
    @Transactional
    void putNonExistingICSRecomm() throws Exception {
        int databaseSizeBeforeUpdate = iCSRecommRepository.findAll().size();
        iCSRecomm.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restICSRecommMockMvc
            .perform(
                put(ENTITY_API_URL_ID, iCSRecomm.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(iCSRecomm))
            )
            .andExpect(status().isBadRequest());

        // Validate the ICSRecomm in the database
        List<ICSRecomm> iCSRecommList = iCSRecommRepository.findAll();
        assertThat(iCSRecommList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ICSRecomm in Elasticsearch
        verify(mockICSRecommSearchRepository, times(0)).save(iCSRecomm);
    }

    @Test
    @Transactional
    void putWithIdMismatchICSRecomm() throws Exception {
        int databaseSizeBeforeUpdate = iCSRecommRepository.findAll().size();
        iCSRecomm.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restICSRecommMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(iCSRecomm))
            )
            .andExpect(status().isBadRequest());

        // Validate the ICSRecomm in the database
        List<ICSRecomm> iCSRecommList = iCSRecommRepository.findAll();
        assertThat(iCSRecommList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ICSRecomm in Elasticsearch
        verify(mockICSRecommSearchRepository, times(0)).save(iCSRecomm);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamICSRecomm() throws Exception {
        int databaseSizeBeforeUpdate = iCSRecommRepository.findAll().size();
        iCSRecomm.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restICSRecommMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(iCSRecomm))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ICSRecomm in the database
        List<ICSRecomm> iCSRecommList = iCSRecommRepository.findAll();
        assertThat(iCSRecommList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ICSRecomm in Elasticsearch
        verify(mockICSRecommSearchRepository, times(0)).save(iCSRecomm);
    }

    @Test
    @Transactional
    void partialUpdateICSRecommWithPatch() throws Exception {
        // Initialize the database
        iCSRecommRepository.saveAndFlush(iCSRecomm);

        int databaseSizeBeforeUpdate = iCSRecommRepository.findAll().size();

        // Update the iCSRecomm using partial update
        ICSRecomm partialUpdatedICSRecomm = new ICSRecomm();
        partialUpdatedICSRecomm.setId(iCSRecomm.getId());

        partialUpdatedICSRecomm.icsNumber(UPDATED_ICS_NUMBER).icsDescr(UPDATED_ICS_DESCR).title(UPDATED_TITLE);

        restICSRecommMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedICSRecomm.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedICSRecomm))
            )
            .andExpect(status().isOk());

        // Validate the ICSRecomm in the database
        List<ICSRecomm> iCSRecommList = iCSRecommRepository.findAll();
        assertThat(iCSRecommList).hasSize(databaseSizeBeforeUpdate);
        ICSRecomm testICSRecomm = iCSRecommList.get(iCSRecommList.size() - 1);
        assertThat(testICSRecomm.getYear()).isEqualTo(DEFAULT_YEAR);
        assertThat(testICSRecomm.getIcsNumber()).isEqualTo(UPDATED_ICS_NUMBER);
        assertThat(testICSRecomm.getIcsDescr()).isEqualTo(UPDATED_ICS_DESCR);
        assertThat(testICSRecomm.getTitle()).isEqualTo(UPDATED_TITLE);
    }

    @Test
    @Transactional
    void fullUpdateICSRecommWithPatch() throws Exception {
        // Initialize the database
        iCSRecommRepository.saveAndFlush(iCSRecomm);

        int databaseSizeBeforeUpdate = iCSRecommRepository.findAll().size();

        // Update the iCSRecomm using partial update
        ICSRecomm partialUpdatedICSRecomm = new ICSRecomm();
        partialUpdatedICSRecomm.setId(iCSRecomm.getId());

        partialUpdatedICSRecomm.year(UPDATED_YEAR).icsNumber(UPDATED_ICS_NUMBER).icsDescr(UPDATED_ICS_DESCR).title(UPDATED_TITLE);

        restICSRecommMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedICSRecomm.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedICSRecomm))
            )
            .andExpect(status().isOk());

        // Validate the ICSRecomm in the database
        List<ICSRecomm> iCSRecommList = iCSRecommRepository.findAll();
        assertThat(iCSRecommList).hasSize(databaseSizeBeforeUpdate);
        ICSRecomm testICSRecomm = iCSRecommList.get(iCSRecommList.size() - 1);
        assertThat(testICSRecomm.getYear()).isEqualTo(UPDATED_YEAR);
        assertThat(testICSRecomm.getIcsNumber()).isEqualTo(UPDATED_ICS_NUMBER);
        assertThat(testICSRecomm.getIcsDescr()).isEqualTo(UPDATED_ICS_DESCR);
        assertThat(testICSRecomm.getTitle()).isEqualTo(UPDATED_TITLE);
    }

    @Test
    @Transactional
    void patchNonExistingICSRecomm() throws Exception {
        int databaseSizeBeforeUpdate = iCSRecommRepository.findAll().size();
        iCSRecomm.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restICSRecommMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, iCSRecomm.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(iCSRecomm))
            )
            .andExpect(status().isBadRequest());

        // Validate the ICSRecomm in the database
        List<ICSRecomm> iCSRecommList = iCSRecommRepository.findAll();
        assertThat(iCSRecommList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ICSRecomm in Elasticsearch
        verify(mockICSRecommSearchRepository, times(0)).save(iCSRecomm);
    }

    @Test
    @Transactional
    void patchWithIdMismatchICSRecomm() throws Exception {
        int databaseSizeBeforeUpdate = iCSRecommRepository.findAll().size();
        iCSRecomm.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restICSRecommMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(iCSRecomm))
            )
            .andExpect(status().isBadRequest());

        // Validate the ICSRecomm in the database
        List<ICSRecomm> iCSRecommList = iCSRecommRepository.findAll();
        assertThat(iCSRecommList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ICSRecomm in Elasticsearch
        verify(mockICSRecommSearchRepository, times(0)).save(iCSRecomm);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamICSRecomm() throws Exception {
        int databaseSizeBeforeUpdate = iCSRecommRepository.findAll().size();
        iCSRecomm.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restICSRecommMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(iCSRecomm))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ICSRecomm in the database
        List<ICSRecomm> iCSRecommList = iCSRecommRepository.findAll();
        assertThat(iCSRecommList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ICSRecomm in Elasticsearch
        verify(mockICSRecommSearchRepository, times(0)).save(iCSRecomm);
    }

    @Test
    @Transactional
    void deleteICSRecomm() throws Exception {
        // Initialize the database
        iCSRecommRepository.saveAndFlush(iCSRecomm);

        int databaseSizeBeforeDelete = iCSRecommRepository.findAll().size();

        // Delete the iCSRecomm
        restICSRecommMockMvc
            .perform(delete(ENTITY_API_URL_ID, iCSRecomm.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ICSRecomm> iCSRecommList = iCSRecommRepository.findAll();
        assertThat(iCSRecommList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the ICSRecomm in Elasticsearch
        verify(mockICSRecommSearchRepository, times(1)).deleteById(iCSRecomm.getId());
    }

    @Test
    @Transactional
    void searchICSRecomm() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        iCSRecommRepository.saveAndFlush(iCSRecomm);
        when(mockICSRecommSearchRepository.search(queryStringQuery("id:" + iCSRecomm.getId())))
            .thenReturn(Collections.singletonList(iCSRecomm));

        // Search the iCSRecomm
        restICSRecommMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + iCSRecomm.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(iCSRecomm.getId().intValue())))
            .andExpect(jsonPath("$.[*].year").value(hasItem(DEFAULT_YEAR)))
            .andExpect(jsonPath("$.[*].icsNumber").value(hasItem(DEFAULT_ICS_NUMBER)))
            .andExpect(jsonPath("$.[*].icsDescr").value(hasItem(DEFAULT_ICS_DESCR)))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)));
    }
}

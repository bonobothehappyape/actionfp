package eu.scram.actionfp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import eu.scram.actionfp.IntegrationTest;
import eu.scram.actionfp.domain.AuditReport;
import eu.scram.actionfp.repository.AuditReportRepository;
import eu.scram.actionfp.repository.search.AuditReportSearchRepository;
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
 * Integration tests for the {@link AuditReportResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AuditReportResourceIT {

    private static final Integer DEFAULT_YEAR = 1;
    private static final Integer UPDATED_YEAR = 2;

    private static final String DEFAULT_REPORT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_REPORT_TITLE = "BBBBBBBBBB";

    private static final Long DEFAULT_INSTITUTION = 1L;
    private static final Long UPDATED_INSTITUTION = 2L;

    private static final String DEFAULT_REPORT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_REPORT_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/audit-reports";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/audit-reports";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AuditReportRepository auditReportRepository;

    /**
     * This repository is mocked in the eu.scram.actionfp.repository.search test package.
     *
     * @see eu.scram.actionfp.repository.search.AuditReportSearchRepositoryMockConfiguration
     */
    @Autowired
    private AuditReportSearchRepository mockAuditReportSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAuditReportMockMvc;

    private AuditReport auditReport;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AuditReport createEntity(EntityManager em) {
        AuditReport auditReport = new AuditReport()
            .year(DEFAULT_YEAR)
            .reportTitle(DEFAULT_REPORT_TITLE)
            .institution(DEFAULT_INSTITUTION)
            .reportDescription(DEFAULT_REPORT_DESCRIPTION);
        return auditReport;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AuditReport createUpdatedEntity(EntityManager em) {
        AuditReport auditReport = new AuditReport()
            .year(UPDATED_YEAR)
            .reportTitle(UPDATED_REPORT_TITLE)
            .institution(UPDATED_INSTITUTION)
            .reportDescription(UPDATED_REPORT_DESCRIPTION);
        return auditReport;
    }

    @BeforeEach
    public void initTest() {
        auditReport = createEntity(em);
    }

    @Test
    @Transactional
    void createAuditReport() throws Exception {
        int databaseSizeBeforeCreate = auditReportRepository.findAll().size();
        // Create the AuditReport
        restAuditReportMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(auditReport))
            )
            .andExpect(status().isCreated());

        // Validate the AuditReport in the database
        List<AuditReport> auditReportList = auditReportRepository.findAll();
        assertThat(auditReportList).hasSize(databaseSizeBeforeCreate + 1);
        AuditReport testAuditReport = auditReportList.get(auditReportList.size() - 1);
        assertThat(testAuditReport.getYear()).isEqualTo(DEFAULT_YEAR);
        assertThat(testAuditReport.getReportTitle()).isEqualTo(DEFAULT_REPORT_TITLE);
        assertThat(testAuditReport.getInstitution()).isEqualTo(DEFAULT_INSTITUTION);
        assertThat(testAuditReport.getReportDescription()).isEqualTo(DEFAULT_REPORT_DESCRIPTION);

        // Validate the AuditReport in Elasticsearch
        verify(mockAuditReportSearchRepository, times(1)).save(testAuditReport);
    }

    @Test
    @Transactional
    void createAuditReportWithExistingId() throws Exception {
        // Create the AuditReport with an existing ID
        auditReport.setId(1L);

        int databaseSizeBeforeCreate = auditReportRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAuditReportMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(auditReport))
            )
            .andExpect(status().isBadRequest());

        // Validate the AuditReport in the database
        List<AuditReport> auditReportList = auditReportRepository.findAll();
        assertThat(auditReportList).hasSize(databaseSizeBeforeCreate);

        // Validate the AuditReport in Elasticsearch
        verify(mockAuditReportSearchRepository, times(0)).save(auditReport);
    }

    @Test
    @Transactional
    void getAllAuditReports() throws Exception {
        // Initialize the database
        auditReportRepository.saveAndFlush(auditReport);

        // Get all the auditReportList
        restAuditReportMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(auditReport.getId().intValue())))
            .andExpect(jsonPath("$.[*].year").value(hasItem(DEFAULT_YEAR)))
            .andExpect(jsonPath("$.[*].reportTitle").value(hasItem(DEFAULT_REPORT_TITLE)))
            .andExpect(jsonPath("$.[*].institution").value(hasItem(DEFAULT_INSTITUTION.intValue())))
            .andExpect(jsonPath("$.[*].reportDescription").value(hasItem(DEFAULT_REPORT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getAuditReport() throws Exception {
        // Initialize the database
        auditReportRepository.saveAndFlush(auditReport);

        // Get the auditReport
        restAuditReportMockMvc
            .perform(get(ENTITY_API_URL_ID, auditReport.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(auditReport.getId().intValue()))
            .andExpect(jsonPath("$.year").value(DEFAULT_YEAR))
            .andExpect(jsonPath("$.reportTitle").value(DEFAULT_REPORT_TITLE))
            .andExpect(jsonPath("$.institution").value(DEFAULT_INSTITUTION.intValue()))
            .andExpect(jsonPath("$.reportDescription").value(DEFAULT_REPORT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingAuditReport() throws Exception {
        // Get the auditReport
        restAuditReportMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAuditReport() throws Exception {
        // Initialize the database
        auditReportRepository.saveAndFlush(auditReport);

        int databaseSizeBeforeUpdate = auditReportRepository.findAll().size();

        // Update the auditReport
        AuditReport updatedAuditReport = auditReportRepository.findById(auditReport.getId()).get();
        // Disconnect from session so that the updates on updatedAuditReport are not directly saved in db
        em.detach(updatedAuditReport);
        updatedAuditReport
            .year(UPDATED_YEAR)
            .reportTitle(UPDATED_REPORT_TITLE)
            .institution(UPDATED_INSTITUTION)
            .reportDescription(UPDATED_REPORT_DESCRIPTION);

        restAuditReportMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAuditReport.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedAuditReport))
            )
            .andExpect(status().isOk());

        // Validate the AuditReport in the database
        List<AuditReport> auditReportList = auditReportRepository.findAll();
        assertThat(auditReportList).hasSize(databaseSizeBeforeUpdate);
        AuditReport testAuditReport = auditReportList.get(auditReportList.size() - 1);
        assertThat(testAuditReport.getYear()).isEqualTo(UPDATED_YEAR);
        assertThat(testAuditReport.getReportTitle()).isEqualTo(UPDATED_REPORT_TITLE);
        assertThat(testAuditReport.getInstitution()).isEqualTo(UPDATED_INSTITUTION);
        assertThat(testAuditReport.getReportDescription()).isEqualTo(UPDATED_REPORT_DESCRIPTION);

        // Validate the AuditReport in Elasticsearch
        verify(mockAuditReportSearchRepository).save(testAuditReport);
    }

    @Test
    @Transactional
    void putNonExistingAuditReport() throws Exception {
        int databaseSizeBeforeUpdate = auditReportRepository.findAll().size();
        auditReport.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAuditReportMockMvc
            .perform(
                put(ENTITY_API_URL_ID, auditReport.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(auditReport))
            )
            .andExpect(status().isBadRequest());

        // Validate the AuditReport in the database
        List<AuditReport> auditReportList = auditReportRepository.findAll();
        assertThat(auditReportList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AuditReport in Elasticsearch
        verify(mockAuditReportSearchRepository, times(0)).save(auditReport);
    }

    @Test
    @Transactional
    void putWithIdMismatchAuditReport() throws Exception {
        int databaseSizeBeforeUpdate = auditReportRepository.findAll().size();
        auditReport.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuditReportMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(auditReport))
            )
            .andExpect(status().isBadRequest());

        // Validate the AuditReport in the database
        List<AuditReport> auditReportList = auditReportRepository.findAll();
        assertThat(auditReportList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AuditReport in Elasticsearch
        verify(mockAuditReportSearchRepository, times(0)).save(auditReport);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAuditReport() throws Exception {
        int databaseSizeBeforeUpdate = auditReportRepository.findAll().size();
        auditReport.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuditReportMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(auditReport))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AuditReport in the database
        List<AuditReport> auditReportList = auditReportRepository.findAll();
        assertThat(auditReportList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AuditReport in Elasticsearch
        verify(mockAuditReportSearchRepository, times(0)).save(auditReport);
    }

    @Test
    @Transactional
    void partialUpdateAuditReportWithPatch() throws Exception {
        // Initialize the database
        auditReportRepository.saveAndFlush(auditReport);

        int databaseSizeBeforeUpdate = auditReportRepository.findAll().size();

        // Update the auditReport using partial update
        AuditReport partialUpdatedAuditReport = new AuditReport();
        partialUpdatedAuditReport.setId(auditReport.getId());

        partialUpdatedAuditReport.year(UPDATED_YEAR).reportDescription(UPDATED_REPORT_DESCRIPTION);

        restAuditReportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAuditReport.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAuditReport))
            )
            .andExpect(status().isOk());

        // Validate the AuditReport in the database
        List<AuditReport> auditReportList = auditReportRepository.findAll();
        assertThat(auditReportList).hasSize(databaseSizeBeforeUpdate);
        AuditReport testAuditReport = auditReportList.get(auditReportList.size() - 1);
        assertThat(testAuditReport.getYear()).isEqualTo(UPDATED_YEAR);
        assertThat(testAuditReport.getReportTitle()).isEqualTo(DEFAULT_REPORT_TITLE);
        assertThat(testAuditReport.getInstitution()).isEqualTo(DEFAULT_INSTITUTION);
        assertThat(testAuditReport.getReportDescription()).isEqualTo(UPDATED_REPORT_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateAuditReportWithPatch() throws Exception {
        // Initialize the database
        auditReportRepository.saveAndFlush(auditReport);

        int databaseSizeBeforeUpdate = auditReportRepository.findAll().size();

        // Update the auditReport using partial update
        AuditReport partialUpdatedAuditReport = new AuditReport();
        partialUpdatedAuditReport.setId(auditReport.getId());

        partialUpdatedAuditReport
            .year(UPDATED_YEAR)
            .reportTitle(UPDATED_REPORT_TITLE)
            .institution(UPDATED_INSTITUTION)
            .reportDescription(UPDATED_REPORT_DESCRIPTION);

        restAuditReportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAuditReport.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAuditReport))
            )
            .andExpect(status().isOk());

        // Validate the AuditReport in the database
        List<AuditReport> auditReportList = auditReportRepository.findAll();
        assertThat(auditReportList).hasSize(databaseSizeBeforeUpdate);
        AuditReport testAuditReport = auditReportList.get(auditReportList.size() - 1);
        assertThat(testAuditReport.getYear()).isEqualTo(UPDATED_YEAR);
        assertThat(testAuditReport.getReportTitle()).isEqualTo(UPDATED_REPORT_TITLE);
        assertThat(testAuditReport.getInstitution()).isEqualTo(UPDATED_INSTITUTION);
        assertThat(testAuditReport.getReportDescription()).isEqualTo(UPDATED_REPORT_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingAuditReport() throws Exception {
        int databaseSizeBeforeUpdate = auditReportRepository.findAll().size();
        auditReport.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAuditReportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, auditReport.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(auditReport))
            )
            .andExpect(status().isBadRequest());

        // Validate the AuditReport in the database
        List<AuditReport> auditReportList = auditReportRepository.findAll();
        assertThat(auditReportList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AuditReport in Elasticsearch
        verify(mockAuditReportSearchRepository, times(0)).save(auditReport);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAuditReport() throws Exception {
        int databaseSizeBeforeUpdate = auditReportRepository.findAll().size();
        auditReport.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuditReportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(auditReport))
            )
            .andExpect(status().isBadRequest());

        // Validate the AuditReport in the database
        List<AuditReport> auditReportList = auditReportRepository.findAll();
        assertThat(auditReportList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AuditReport in Elasticsearch
        verify(mockAuditReportSearchRepository, times(0)).save(auditReport);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAuditReport() throws Exception {
        int databaseSizeBeforeUpdate = auditReportRepository.findAll().size();
        auditReport.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuditReportMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(auditReport))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AuditReport in the database
        List<AuditReport> auditReportList = auditReportRepository.findAll();
        assertThat(auditReportList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AuditReport in Elasticsearch
        verify(mockAuditReportSearchRepository, times(0)).save(auditReport);
    }

    @Test
    @Transactional
    void deleteAuditReport() throws Exception {
        // Initialize the database
        auditReportRepository.saveAndFlush(auditReport);

        int databaseSizeBeforeDelete = auditReportRepository.findAll().size();

        // Delete the auditReport
        restAuditReportMockMvc
            .perform(delete(ENTITY_API_URL_ID, auditReport.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<AuditReport> auditReportList = auditReportRepository.findAll();
        assertThat(auditReportList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the AuditReport in Elasticsearch
        verify(mockAuditReportSearchRepository, times(1)).deleteById(auditReport.getId());
    }

    @Test
    @Transactional
    void searchAuditReport() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        auditReportRepository.saveAndFlush(auditReport);
        when(mockAuditReportSearchRepository.search(queryStringQuery("id:" + auditReport.getId())))
            .thenReturn(Collections.singletonList(auditReport));

        // Search the auditReport
        restAuditReportMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + auditReport.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(auditReport.getId().intValue())))
            .andExpect(jsonPath("$.[*].year").value(hasItem(DEFAULT_YEAR)))
            .andExpect(jsonPath("$.[*].reportTitle").value(hasItem(DEFAULT_REPORT_TITLE)))
            .andExpect(jsonPath("$.[*].institution").value(hasItem(DEFAULT_INSTITUTION.intValue())))
            .andExpect(jsonPath("$.[*].reportDescription").value(hasItem(DEFAULT_REPORT_DESCRIPTION)));
    }
}

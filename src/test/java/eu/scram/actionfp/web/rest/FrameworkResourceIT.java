package eu.scram.actionfp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import eu.scram.actionfp.IntegrationTest;
import eu.scram.actionfp.domain.Framework;
import eu.scram.actionfp.repository.FrameworkRepository;
import eu.scram.actionfp.repository.search.FrameworkSearchRepository;
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
 * Integration tests for the {@link FrameworkResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class FrameworkResourceIT {

    private static final Integer DEFAULT_YEAR = 1;
    private static final Integer UPDATED_YEAR = 2;

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_TYPE = 1;
    private static final Integer UPDATED_TYPE = 2;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/frameworks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/frameworks";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FrameworkRepository frameworkRepository;

    /**
     * This repository is mocked in the eu.scram.actionfp.repository.search test package.
     *
     * @see eu.scram.actionfp.repository.search.FrameworkSearchRepositoryMockConfiguration
     */
    @Autowired
    private FrameworkSearchRepository mockFrameworkSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFrameworkMockMvc;

    private Framework framework;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Framework createEntity(EntityManager em) {
        Framework framework = new Framework().year(DEFAULT_YEAR).name(DEFAULT_NAME).type(DEFAULT_TYPE).description(DEFAULT_DESCRIPTION);
        return framework;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Framework createUpdatedEntity(EntityManager em) {
        Framework framework = new Framework().year(UPDATED_YEAR).name(UPDATED_NAME).type(UPDATED_TYPE).description(UPDATED_DESCRIPTION);
        return framework;
    }

    @BeforeEach
    public void initTest() {
        framework = createEntity(em);
    }

    @Test
    @Transactional
    void createFramework() throws Exception {
        int databaseSizeBeforeCreate = frameworkRepository.findAll().size();
        // Create the Framework
        restFrameworkMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(framework))
            )
            .andExpect(status().isCreated());

        // Validate the Framework in the database
        List<Framework> frameworkList = frameworkRepository.findAll();
        assertThat(frameworkList).hasSize(databaseSizeBeforeCreate + 1);
        Framework testFramework = frameworkList.get(frameworkList.size() - 1);
        assertThat(testFramework.getYear()).isEqualTo(DEFAULT_YEAR);
        assertThat(testFramework.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testFramework.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testFramework.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the Framework in Elasticsearch
        verify(mockFrameworkSearchRepository, times(1)).save(testFramework);
    }

    @Test
    @Transactional
    void createFrameworkWithExistingId() throws Exception {
        // Create the Framework with an existing ID
        framework.setId(1L);

        int databaseSizeBeforeCreate = frameworkRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFrameworkMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(framework))
            )
            .andExpect(status().isBadRequest());

        // Validate the Framework in the database
        List<Framework> frameworkList = frameworkRepository.findAll();
        assertThat(frameworkList).hasSize(databaseSizeBeforeCreate);

        // Validate the Framework in Elasticsearch
        verify(mockFrameworkSearchRepository, times(0)).save(framework);
    }

    @Test
    @Transactional
    void getAllFrameworks() throws Exception {
        // Initialize the database
        frameworkRepository.saveAndFlush(framework);

        // Get all the frameworkList
        restFrameworkMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(framework.getId().intValue())))
            .andExpect(jsonPath("$.[*].year").value(hasItem(DEFAULT_YEAR)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getFramework() throws Exception {
        // Initialize the database
        frameworkRepository.saveAndFlush(framework);

        // Get the framework
        restFrameworkMockMvc
            .perform(get(ENTITY_API_URL_ID, framework.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(framework.getId().intValue()))
            .andExpect(jsonPath("$.year").value(DEFAULT_YEAR))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingFramework() throws Exception {
        // Get the framework
        restFrameworkMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewFramework() throws Exception {
        // Initialize the database
        frameworkRepository.saveAndFlush(framework);

        int databaseSizeBeforeUpdate = frameworkRepository.findAll().size();

        // Update the framework
        Framework updatedFramework = frameworkRepository.findById(framework.getId()).get();
        // Disconnect from session so that the updates on updatedFramework are not directly saved in db
        em.detach(updatedFramework);
        updatedFramework.year(UPDATED_YEAR).name(UPDATED_NAME).type(UPDATED_TYPE).description(UPDATED_DESCRIPTION);

        restFrameworkMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedFramework.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedFramework))
            )
            .andExpect(status().isOk());

        // Validate the Framework in the database
        List<Framework> frameworkList = frameworkRepository.findAll();
        assertThat(frameworkList).hasSize(databaseSizeBeforeUpdate);
        Framework testFramework = frameworkList.get(frameworkList.size() - 1);
        assertThat(testFramework.getYear()).isEqualTo(UPDATED_YEAR);
        assertThat(testFramework.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFramework.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testFramework.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the Framework in Elasticsearch
        verify(mockFrameworkSearchRepository).save(testFramework);
    }

    @Test
    @Transactional
    void putNonExistingFramework() throws Exception {
        int databaseSizeBeforeUpdate = frameworkRepository.findAll().size();
        framework.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFrameworkMockMvc
            .perform(
                put(ENTITY_API_URL_ID, framework.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(framework))
            )
            .andExpect(status().isBadRequest());

        // Validate the Framework in the database
        List<Framework> frameworkList = frameworkRepository.findAll();
        assertThat(frameworkList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Framework in Elasticsearch
        verify(mockFrameworkSearchRepository, times(0)).save(framework);
    }

    @Test
    @Transactional
    void putWithIdMismatchFramework() throws Exception {
        int databaseSizeBeforeUpdate = frameworkRepository.findAll().size();
        framework.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFrameworkMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(framework))
            )
            .andExpect(status().isBadRequest());

        // Validate the Framework in the database
        List<Framework> frameworkList = frameworkRepository.findAll();
        assertThat(frameworkList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Framework in Elasticsearch
        verify(mockFrameworkSearchRepository, times(0)).save(framework);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFramework() throws Exception {
        int databaseSizeBeforeUpdate = frameworkRepository.findAll().size();
        framework.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFrameworkMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(framework))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Framework in the database
        List<Framework> frameworkList = frameworkRepository.findAll();
        assertThat(frameworkList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Framework in Elasticsearch
        verify(mockFrameworkSearchRepository, times(0)).save(framework);
    }

    @Test
    @Transactional
    void partialUpdateFrameworkWithPatch() throws Exception {
        // Initialize the database
        frameworkRepository.saveAndFlush(framework);

        int databaseSizeBeforeUpdate = frameworkRepository.findAll().size();

        // Update the framework using partial update
        Framework partialUpdatedFramework = new Framework();
        partialUpdatedFramework.setId(framework.getId());

        partialUpdatedFramework.year(UPDATED_YEAR).name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restFrameworkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFramework.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFramework))
            )
            .andExpect(status().isOk());

        // Validate the Framework in the database
        List<Framework> frameworkList = frameworkRepository.findAll();
        assertThat(frameworkList).hasSize(databaseSizeBeforeUpdate);
        Framework testFramework = frameworkList.get(frameworkList.size() - 1);
        assertThat(testFramework.getYear()).isEqualTo(UPDATED_YEAR);
        assertThat(testFramework.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFramework.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testFramework.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateFrameworkWithPatch() throws Exception {
        // Initialize the database
        frameworkRepository.saveAndFlush(framework);

        int databaseSizeBeforeUpdate = frameworkRepository.findAll().size();

        // Update the framework using partial update
        Framework partialUpdatedFramework = new Framework();
        partialUpdatedFramework.setId(framework.getId());

        partialUpdatedFramework.year(UPDATED_YEAR).name(UPDATED_NAME).type(UPDATED_TYPE).description(UPDATED_DESCRIPTION);

        restFrameworkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFramework.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFramework))
            )
            .andExpect(status().isOk());

        // Validate the Framework in the database
        List<Framework> frameworkList = frameworkRepository.findAll();
        assertThat(frameworkList).hasSize(databaseSizeBeforeUpdate);
        Framework testFramework = frameworkList.get(frameworkList.size() - 1);
        assertThat(testFramework.getYear()).isEqualTo(UPDATED_YEAR);
        assertThat(testFramework.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFramework.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testFramework.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingFramework() throws Exception {
        int databaseSizeBeforeUpdate = frameworkRepository.findAll().size();
        framework.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFrameworkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, framework.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(framework))
            )
            .andExpect(status().isBadRequest());

        // Validate the Framework in the database
        List<Framework> frameworkList = frameworkRepository.findAll();
        assertThat(frameworkList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Framework in Elasticsearch
        verify(mockFrameworkSearchRepository, times(0)).save(framework);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFramework() throws Exception {
        int databaseSizeBeforeUpdate = frameworkRepository.findAll().size();
        framework.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFrameworkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(framework))
            )
            .andExpect(status().isBadRequest());

        // Validate the Framework in the database
        List<Framework> frameworkList = frameworkRepository.findAll();
        assertThat(frameworkList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Framework in Elasticsearch
        verify(mockFrameworkSearchRepository, times(0)).save(framework);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFramework() throws Exception {
        int databaseSizeBeforeUpdate = frameworkRepository.findAll().size();
        framework.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFrameworkMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(framework))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Framework in the database
        List<Framework> frameworkList = frameworkRepository.findAll();
        assertThat(frameworkList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Framework in Elasticsearch
        verify(mockFrameworkSearchRepository, times(0)).save(framework);
    }

    @Test
    @Transactional
    void deleteFramework() throws Exception {
        // Initialize the database
        frameworkRepository.saveAndFlush(framework);

        int databaseSizeBeforeDelete = frameworkRepository.findAll().size();

        // Delete the framework
        restFrameworkMockMvc
            .perform(delete(ENTITY_API_URL_ID, framework.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Framework> frameworkList = frameworkRepository.findAll();
        assertThat(frameworkList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Framework in Elasticsearch
        verify(mockFrameworkSearchRepository, times(1)).deleteById(framework.getId());
    }

    @Test
    @Transactional
    void searchFramework() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        frameworkRepository.saveAndFlush(framework);
        when(mockFrameworkSearchRepository.search(queryStringQuery("id:" + framework.getId())))
            .thenReturn(Collections.singletonList(framework));

        // Search the framework
        restFrameworkMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + framework.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(framework.getId().intValue())))
            .andExpect(jsonPath("$.[*].year").value(hasItem(DEFAULT_YEAR)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }
}

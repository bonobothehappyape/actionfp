package eu.scram.actionfp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import eu.scram.actionfp.IntegrationTest;
import eu.scram.actionfp.domain.ActionAttachment;
import eu.scram.actionfp.repository.ActionAttachmentRepository;
import eu.scram.actionfp.repository.search.ActionAttachmentSearchRepository;
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
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link ActionAttachmentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ActionAttachmentResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_MIME_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_MIME_TYPE = "BBBBBBBBBB";

    private static final byte[] DEFAULT_ATTACHED_FILE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_ATTACHED_FILE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_ATTACHED_FILE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_ATTACHED_FILE_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/action-attachments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/action-attachments";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ActionAttachmentRepository actionAttachmentRepository;

    /**
     * This repository is mocked in the eu.scram.actionfp.repository.search test package.
     *
     * @see eu.scram.actionfp.repository.search.ActionAttachmentSearchRepositoryMockConfiguration
     */
    @Autowired
    private ActionAttachmentSearchRepository mockActionAttachmentSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restActionAttachmentMockMvc;

    private ActionAttachment actionAttachment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ActionAttachment createEntity(EntityManager em) {
        ActionAttachment actionAttachment = new ActionAttachment()
            .name(DEFAULT_NAME)
            .mimeType(DEFAULT_MIME_TYPE)
            .attachedFile(DEFAULT_ATTACHED_FILE)
            .attachedFileContentType(DEFAULT_ATTACHED_FILE_CONTENT_TYPE)
            .url(DEFAULT_URL);
        return actionAttachment;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ActionAttachment createUpdatedEntity(EntityManager em) {
        ActionAttachment actionAttachment = new ActionAttachment()
            .name(UPDATED_NAME)
            .mimeType(UPDATED_MIME_TYPE)
            .attachedFile(UPDATED_ATTACHED_FILE)
            .attachedFileContentType(UPDATED_ATTACHED_FILE_CONTENT_TYPE)
            .url(UPDATED_URL);
        return actionAttachment;
    }

    @BeforeEach
    public void initTest() {
        actionAttachment = createEntity(em);
    }

    @Test
    @Transactional
    void createActionAttachment() throws Exception {
        int databaseSizeBeforeCreate = actionAttachmentRepository.findAll().size();
        // Create the ActionAttachment
        restActionAttachmentMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(actionAttachment))
            )
            .andExpect(status().isCreated());

        // Validate the ActionAttachment in the database
        List<ActionAttachment> actionAttachmentList = actionAttachmentRepository.findAll();
        assertThat(actionAttachmentList).hasSize(databaseSizeBeforeCreate + 1);
        ActionAttachment testActionAttachment = actionAttachmentList.get(actionAttachmentList.size() - 1);
        assertThat(testActionAttachment.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testActionAttachment.getMimeType()).isEqualTo(DEFAULT_MIME_TYPE);
        assertThat(testActionAttachment.getAttachedFile()).isEqualTo(DEFAULT_ATTACHED_FILE);
        assertThat(testActionAttachment.getAttachedFileContentType()).isEqualTo(DEFAULT_ATTACHED_FILE_CONTENT_TYPE);
        assertThat(testActionAttachment.getUrl()).isEqualTo(DEFAULT_URL);

        // Validate the ActionAttachment in Elasticsearch
        verify(mockActionAttachmentSearchRepository, times(1)).save(testActionAttachment);
    }

    @Test
    @Transactional
    void createActionAttachmentWithExistingId() throws Exception {
        // Create the ActionAttachment with an existing ID
        actionAttachment.setId(1L);

        int databaseSizeBeforeCreate = actionAttachmentRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restActionAttachmentMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(actionAttachment))
            )
            .andExpect(status().isBadRequest());

        // Validate the ActionAttachment in the database
        List<ActionAttachment> actionAttachmentList = actionAttachmentRepository.findAll();
        assertThat(actionAttachmentList).hasSize(databaseSizeBeforeCreate);

        // Validate the ActionAttachment in Elasticsearch
        verify(mockActionAttachmentSearchRepository, times(0)).save(actionAttachment);
    }

    @Test
    @Transactional
    void getAllActionAttachments() throws Exception {
        // Initialize the database
        actionAttachmentRepository.saveAndFlush(actionAttachment);

        // Get all the actionAttachmentList
        restActionAttachmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(actionAttachment.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].mimeType").value(hasItem(DEFAULT_MIME_TYPE)))
            .andExpect(jsonPath("$.[*].attachedFileContentType").value(hasItem(DEFAULT_ATTACHED_FILE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].attachedFile").value(hasItem(Base64Utils.encodeToString(DEFAULT_ATTACHED_FILE))))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)));
    }

    @Test
    @Transactional
    void getActionAttachment() throws Exception {
        // Initialize the database
        actionAttachmentRepository.saveAndFlush(actionAttachment);

        // Get the actionAttachment
        restActionAttachmentMockMvc
            .perform(get(ENTITY_API_URL_ID, actionAttachment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(actionAttachment.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.mimeType").value(DEFAULT_MIME_TYPE))
            .andExpect(jsonPath("$.attachedFileContentType").value(DEFAULT_ATTACHED_FILE_CONTENT_TYPE))
            .andExpect(jsonPath("$.attachedFile").value(Base64Utils.encodeToString(DEFAULT_ATTACHED_FILE)))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL));
    }

    @Test
    @Transactional
    void getNonExistingActionAttachment() throws Exception {
        // Get the actionAttachment
        restActionAttachmentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewActionAttachment() throws Exception {
        // Initialize the database
        actionAttachmentRepository.saveAndFlush(actionAttachment);

        int databaseSizeBeforeUpdate = actionAttachmentRepository.findAll().size();

        // Update the actionAttachment
        ActionAttachment updatedActionAttachment = actionAttachmentRepository.findById(actionAttachment.getId()).get();
        // Disconnect from session so that the updates on updatedActionAttachment are not directly saved in db
        em.detach(updatedActionAttachment);
        updatedActionAttachment
            .name(UPDATED_NAME)
            .mimeType(UPDATED_MIME_TYPE)
            .attachedFile(UPDATED_ATTACHED_FILE)
            .attachedFileContentType(UPDATED_ATTACHED_FILE_CONTENT_TYPE)
            .url(UPDATED_URL);

        restActionAttachmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedActionAttachment.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedActionAttachment))
            )
            .andExpect(status().isOk());

        // Validate the ActionAttachment in the database
        List<ActionAttachment> actionAttachmentList = actionAttachmentRepository.findAll();
        assertThat(actionAttachmentList).hasSize(databaseSizeBeforeUpdate);
        ActionAttachment testActionAttachment = actionAttachmentList.get(actionAttachmentList.size() - 1);
        assertThat(testActionAttachment.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testActionAttachment.getMimeType()).isEqualTo(UPDATED_MIME_TYPE);
        assertThat(testActionAttachment.getAttachedFile()).isEqualTo(UPDATED_ATTACHED_FILE);
        assertThat(testActionAttachment.getAttachedFileContentType()).isEqualTo(UPDATED_ATTACHED_FILE_CONTENT_TYPE);
        assertThat(testActionAttachment.getUrl()).isEqualTo(UPDATED_URL);

        // Validate the ActionAttachment in Elasticsearch
        verify(mockActionAttachmentSearchRepository).save(testActionAttachment);
    }

    @Test
    @Transactional
    void putNonExistingActionAttachment() throws Exception {
        int databaseSizeBeforeUpdate = actionAttachmentRepository.findAll().size();
        actionAttachment.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restActionAttachmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, actionAttachment.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(actionAttachment))
            )
            .andExpect(status().isBadRequest());

        // Validate the ActionAttachment in the database
        List<ActionAttachment> actionAttachmentList = actionAttachmentRepository.findAll();
        assertThat(actionAttachmentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ActionAttachment in Elasticsearch
        verify(mockActionAttachmentSearchRepository, times(0)).save(actionAttachment);
    }

    @Test
    @Transactional
    void putWithIdMismatchActionAttachment() throws Exception {
        int databaseSizeBeforeUpdate = actionAttachmentRepository.findAll().size();
        actionAttachment.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActionAttachmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(actionAttachment))
            )
            .andExpect(status().isBadRequest());

        // Validate the ActionAttachment in the database
        List<ActionAttachment> actionAttachmentList = actionAttachmentRepository.findAll();
        assertThat(actionAttachmentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ActionAttachment in Elasticsearch
        verify(mockActionAttachmentSearchRepository, times(0)).save(actionAttachment);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamActionAttachment() throws Exception {
        int databaseSizeBeforeUpdate = actionAttachmentRepository.findAll().size();
        actionAttachment.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActionAttachmentMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(actionAttachment))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ActionAttachment in the database
        List<ActionAttachment> actionAttachmentList = actionAttachmentRepository.findAll();
        assertThat(actionAttachmentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ActionAttachment in Elasticsearch
        verify(mockActionAttachmentSearchRepository, times(0)).save(actionAttachment);
    }

    @Test
    @Transactional
    void partialUpdateActionAttachmentWithPatch() throws Exception {
        // Initialize the database
        actionAttachmentRepository.saveAndFlush(actionAttachment);

        int databaseSizeBeforeUpdate = actionAttachmentRepository.findAll().size();

        // Update the actionAttachment using partial update
        ActionAttachment partialUpdatedActionAttachment = new ActionAttachment();
        partialUpdatedActionAttachment.setId(actionAttachment.getId());

        partialUpdatedActionAttachment.name(UPDATED_NAME).mimeType(UPDATED_MIME_TYPE);

        restActionAttachmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedActionAttachment.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedActionAttachment))
            )
            .andExpect(status().isOk());

        // Validate the ActionAttachment in the database
        List<ActionAttachment> actionAttachmentList = actionAttachmentRepository.findAll();
        assertThat(actionAttachmentList).hasSize(databaseSizeBeforeUpdate);
        ActionAttachment testActionAttachment = actionAttachmentList.get(actionAttachmentList.size() - 1);
        assertThat(testActionAttachment.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testActionAttachment.getMimeType()).isEqualTo(UPDATED_MIME_TYPE);
        assertThat(testActionAttachment.getAttachedFile()).isEqualTo(DEFAULT_ATTACHED_FILE);
        assertThat(testActionAttachment.getAttachedFileContentType()).isEqualTo(DEFAULT_ATTACHED_FILE_CONTENT_TYPE);
        assertThat(testActionAttachment.getUrl()).isEqualTo(DEFAULT_URL);
    }

    @Test
    @Transactional
    void fullUpdateActionAttachmentWithPatch() throws Exception {
        // Initialize the database
        actionAttachmentRepository.saveAndFlush(actionAttachment);

        int databaseSizeBeforeUpdate = actionAttachmentRepository.findAll().size();

        // Update the actionAttachment using partial update
        ActionAttachment partialUpdatedActionAttachment = new ActionAttachment();
        partialUpdatedActionAttachment.setId(actionAttachment.getId());

        partialUpdatedActionAttachment
            .name(UPDATED_NAME)
            .mimeType(UPDATED_MIME_TYPE)
            .attachedFile(UPDATED_ATTACHED_FILE)
            .attachedFileContentType(UPDATED_ATTACHED_FILE_CONTENT_TYPE)
            .url(UPDATED_URL);

        restActionAttachmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedActionAttachment.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedActionAttachment))
            )
            .andExpect(status().isOk());

        // Validate the ActionAttachment in the database
        List<ActionAttachment> actionAttachmentList = actionAttachmentRepository.findAll();
        assertThat(actionAttachmentList).hasSize(databaseSizeBeforeUpdate);
        ActionAttachment testActionAttachment = actionAttachmentList.get(actionAttachmentList.size() - 1);
        assertThat(testActionAttachment.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testActionAttachment.getMimeType()).isEqualTo(UPDATED_MIME_TYPE);
        assertThat(testActionAttachment.getAttachedFile()).isEqualTo(UPDATED_ATTACHED_FILE);
        assertThat(testActionAttachment.getAttachedFileContentType()).isEqualTo(UPDATED_ATTACHED_FILE_CONTENT_TYPE);
        assertThat(testActionAttachment.getUrl()).isEqualTo(UPDATED_URL);
    }

    @Test
    @Transactional
    void patchNonExistingActionAttachment() throws Exception {
        int databaseSizeBeforeUpdate = actionAttachmentRepository.findAll().size();
        actionAttachment.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restActionAttachmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, actionAttachment.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(actionAttachment))
            )
            .andExpect(status().isBadRequest());

        // Validate the ActionAttachment in the database
        List<ActionAttachment> actionAttachmentList = actionAttachmentRepository.findAll();
        assertThat(actionAttachmentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ActionAttachment in Elasticsearch
        verify(mockActionAttachmentSearchRepository, times(0)).save(actionAttachment);
    }

    @Test
    @Transactional
    void patchWithIdMismatchActionAttachment() throws Exception {
        int databaseSizeBeforeUpdate = actionAttachmentRepository.findAll().size();
        actionAttachment.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActionAttachmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(actionAttachment))
            )
            .andExpect(status().isBadRequest());

        // Validate the ActionAttachment in the database
        List<ActionAttachment> actionAttachmentList = actionAttachmentRepository.findAll();
        assertThat(actionAttachmentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ActionAttachment in Elasticsearch
        verify(mockActionAttachmentSearchRepository, times(0)).save(actionAttachment);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamActionAttachment() throws Exception {
        int databaseSizeBeforeUpdate = actionAttachmentRepository.findAll().size();
        actionAttachment.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActionAttachmentMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(actionAttachment))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ActionAttachment in the database
        List<ActionAttachment> actionAttachmentList = actionAttachmentRepository.findAll();
        assertThat(actionAttachmentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ActionAttachment in Elasticsearch
        verify(mockActionAttachmentSearchRepository, times(0)).save(actionAttachment);
    }

    @Test
    @Transactional
    void deleteActionAttachment() throws Exception {
        // Initialize the database
        actionAttachmentRepository.saveAndFlush(actionAttachment);

        int databaseSizeBeforeDelete = actionAttachmentRepository.findAll().size();

        // Delete the actionAttachment
        restActionAttachmentMockMvc
            .perform(delete(ENTITY_API_URL_ID, actionAttachment.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ActionAttachment> actionAttachmentList = actionAttachmentRepository.findAll();
        assertThat(actionAttachmentList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the ActionAttachment in Elasticsearch
        verify(mockActionAttachmentSearchRepository, times(1)).deleteById(actionAttachment.getId());
    }

    @Test
    @Transactional
    void searchActionAttachment() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        actionAttachmentRepository.saveAndFlush(actionAttachment);
        when(mockActionAttachmentSearchRepository.search(queryStringQuery("id:" + actionAttachment.getId())))
            .thenReturn(Collections.singletonList(actionAttachment));

        // Search the actionAttachment
        restActionAttachmentMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + actionAttachment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(actionAttachment.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].mimeType").value(hasItem(DEFAULT_MIME_TYPE)))
            .andExpect(jsonPath("$.[*].attachedFileContentType").value(hasItem(DEFAULT_ATTACHED_FILE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].attachedFile").value(hasItem(Base64Utils.encodeToString(DEFAULT_ATTACHED_FILE))))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)));
    }
}

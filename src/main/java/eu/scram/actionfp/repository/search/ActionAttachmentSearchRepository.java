package eu.scram.actionfp.repository.search;

import eu.scram.actionfp.domain.ActionAttachment;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link ActionAttachment} entity.
 */
public interface ActionAttachmentSearchRepository extends ElasticsearchRepository<ActionAttachment, Long> {}

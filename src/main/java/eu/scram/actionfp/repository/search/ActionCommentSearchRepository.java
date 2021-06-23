package eu.scram.actionfp.repository.search;

import eu.scram.actionfp.domain.ActionComment;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link ActionComment} entity.
 */
public interface ActionCommentSearchRepository extends ElasticsearchRepository<ActionComment, Long> {}

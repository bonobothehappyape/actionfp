package eu.scram.actionfp.repository.search;

import eu.scram.actionfp.domain.Action;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Action} entity.
 */
public interface ActionSearchRepository extends ElasticsearchRepository<Action, Long> {}

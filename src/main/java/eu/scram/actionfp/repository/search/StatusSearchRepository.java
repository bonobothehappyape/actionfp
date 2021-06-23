package eu.scram.actionfp.repository.search;

import eu.scram.actionfp.domain.Status;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Status} entity.
 */
public interface StatusSearchRepository extends ElasticsearchRepository<Status, Long> {}

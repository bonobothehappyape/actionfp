package eu.scram.actionfp.repository.search;

import eu.scram.actionfp.domain.Framework;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Framework} entity.
 */
public interface FrameworkSearchRepository extends ElasticsearchRepository<Framework, Long> {}

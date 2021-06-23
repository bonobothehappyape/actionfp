package eu.scram.actionfp.repository.search;

import eu.scram.actionfp.domain.AuditRecomm;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link AuditRecomm} entity.
 */
public interface AuditRecommSearchRepository extends ElasticsearchRepository<AuditRecomm, Long> {}

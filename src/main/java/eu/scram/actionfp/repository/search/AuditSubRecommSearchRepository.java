package eu.scram.actionfp.repository.search;

import eu.scram.actionfp.domain.AuditSubRecomm;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link AuditSubRecomm} entity.
 */
public interface AuditSubRecommSearchRepository extends ElasticsearchRepository<AuditSubRecomm, Long> {}

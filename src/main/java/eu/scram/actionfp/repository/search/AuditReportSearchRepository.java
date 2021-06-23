package eu.scram.actionfp.repository.search;

import eu.scram.actionfp.domain.AuditReport;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link AuditReport} entity.
 */
public interface AuditReportSearchRepository extends ElasticsearchRepository<AuditReport, Long> {}

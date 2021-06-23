package eu.scram.actionfp.repository.search;

import eu.scram.actionfp.domain.ActionChangeMail;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link ActionChangeMail} entity.
 */
public interface ActionChangeMailSearchRepository extends ElasticsearchRepository<ActionChangeMail, Long> {}

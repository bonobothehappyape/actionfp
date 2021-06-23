package eu.scram.actionfp.repository.search;

import eu.scram.actionfp.domain.ICSRecomm;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link ICSRecomm} entity.
 */
public interface ICSRecommSearchRepository extends ElasticsearchRepository<ICSRecomm, Long> {}

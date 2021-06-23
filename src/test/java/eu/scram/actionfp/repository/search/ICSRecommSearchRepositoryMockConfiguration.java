package eu.scram.actionfp.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link ICSRecommSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class ICSRecommSearchRepositoryMockConfiguration {

    @MockBean
    private ICSRecommSearchRepository mockICSRecommSearchRepository;
}

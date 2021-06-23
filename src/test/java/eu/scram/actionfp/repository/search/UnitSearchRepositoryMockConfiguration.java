package eu.scram.actionfp.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link UnitSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class UnitSearchRepositoryMockConfiguration {

    @MockBean
    private UnitSearchRepository mockUnitSearchRepository;
}

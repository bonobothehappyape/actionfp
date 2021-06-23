package eu.scram.actionfp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import eu.scram.actionfp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FrameworkTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Framework.class);
        Framework framework1 = new Framework();
        framework1.setId(1L);
        Framework framework2 = new Framework();
        framework2.setId(framework1.getId());
        assertThat(framework1).isEqualTo(framework2);
        framework2.setId(2L);
        assertThat(framework1).isNotEqualTo(framework2);
        framework1.setId(null);
        assertThat(framework1).isNotEqualTo(framework2);
    }
}

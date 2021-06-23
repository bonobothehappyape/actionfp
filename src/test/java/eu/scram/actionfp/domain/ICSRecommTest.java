package eu.scram.actionfp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import eu.scram.actionfp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ICSRecommTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ICSRecomm.class);
        ICSRecomm iCSRecomm1 = new ICSRecomm();
        iCSRecomm1.setId(1L);
        ICSRecomm iCSRecomm2 = new ICSRecomm();
        iCSRecomm2.setId(iCSRecomm1.getId());
        assertThat(iCSRecomm1).isEqualTo(iCSRecomm2);
        iCSRecomm2.setId(2L);
        assertThat(iCSRecomm1).isNotEqualTo(iCSRecomm2);
        iCSRecomm1.setId(null);
        assertThat(iCSRecomm1).isNotEqualTo(iCSRecomm2);
    }
}

package eu.scram.actionfp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import eu.scram.actionfp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AuditRecommTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AuditRecomm.class);
        AuditRecomm auditRecomm1 = new AuditRecomm();
        auditRecomm1.setId(1L);
        AuditRecomm auditRecomm2 = new AuditRecomm();
        auditRecomm2.setId(auditRecomm1.getId());
        assertThat(auditRecomm1).isEqualTo(auditRecomm2);
        auditRecomm2.setId(2L);
        assertThat(auditRecomm1).isNotEqualTo(auditRecomm2);
        auditRecomm1.setId(null);
        assertThat(auditRecomm1).isNotEqualTo(auditRecomm2);
    }
}

package eu.scram.actionfp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import eu.scram.actionfp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AuditSubRecommTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AuditSubRecomm.class);
        AuditSubRecomm auditSubRecomm1 = new AuditSubRecomm();
        auditSubRecomm1.setId(1L);
        AuditSubRecomm auditSubRecomm2 = new AuditSubRecomm();
        auditSubRecomm2.setId(auditSubRecomm1.getId());
        assertThat(auditSubRecomm1).isEqualTo(auditSubRecomm2);
        auditSubRecomm2.setId(2L);
        assertThat(auditSubRecomm1).isNotEqualTo(auditSubRecomm2);
        auditSubRecomm1.setId(null);
        assertThat(auditSubRecomm1).isNotEqualTo(auditSubRecomm2);
    }
}

package eu.scram.actionfp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import eu.scram.actionfp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AuditReportTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AuditReport.class);
        AuditReport auditReport1 = new AuditReport();
        auditReport1.setId(1L);
        AuditReport auditReport2 = new AuditReport();
        auditReport2.setId(auditReport1.getId());
        assertThat(auditReport1).isEqualTo(auditReport2);
        auditReport2.setId(2L);
        assertThat(auditReport1).isNotEqualTo(auditReport2);
        auditReport1.setId(null);
        assertThat(auditReport1).isNotEqualTo(auditReport2);
    }
}

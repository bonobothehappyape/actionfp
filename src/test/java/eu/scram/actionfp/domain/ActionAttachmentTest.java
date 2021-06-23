package eu.scram.actionfp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import eu.scram.actionfp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ActionAttachmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ActionAttachment.class);
        ActionAttachment actionAttachment1 = new ActionAttachment();
        actionAttachment1.setId(1L);
        ActionAttachment actionAttachment2 = new ActionAttachment();
        actionAttachment2.setId(actionAttachment1.getId());
        assertThat(actionAttachment1).isEqualTo(actionAttachment2);
        actionAttachment2.setId(2L);
        assertThat(actionAttachment1).isNotEqualTo(actionAttachment2);
        actionAttachment1.setId(null);
        assertThat(actionAttachment1).isNotEqualTo(actionAttachment2);
    }
}

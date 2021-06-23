package eu.scram.actionfp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import eu.scram.actionfp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ActionChangeMailTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ActionChangeMail.class);
        ActionChangeMail actionChangeMail1 = new ActionChangeMail();
        actionChangeMail1.setId(1L);
        ActionChangeMail actionChangeMail2 = new ActionChangeMail();
        actionChangeMail2.setId(actionChangeMail1.getId());
        assertThat(actionChangeMail1).isEqualTo(actionChangeMail2);
        actionChangeMail2.setId(2L);
        assertThat(actionChangeMail1).isNotEqualTo(actionChangeMail2);
        actionChangeMail1.setId(null);
        assertThat(actionChangeMail1).isNotEqualTo(actionChangeMail2);
    }
}

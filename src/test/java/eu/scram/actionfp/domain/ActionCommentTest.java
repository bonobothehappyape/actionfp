package eu.scram.actionfp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import eu.scram.actionfp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ActionCommentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ActionComment.class);
        ActionComment actionComment1 = new ActionComment();
        actionComment1.setId(1L);
        ActionComment actionComment2 = new ActionComment();
        actionComment2.setId(actionComment1.getId());
        assertThat(actionComment1).isEqualTo(actionComment2);
        actionComment2.setId(2L);
        assertThat(actionComment1).isNotEqualTo(actionComment2);
        actionComment1.setId(null);
        assertThat(actionComment1).isNotEqualTo(actionComment2);
    }
}

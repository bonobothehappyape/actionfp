package eu.scram.actionfp.repository;

import eu.scram.actionfp.domain.ActionChangeMail;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the ActionChangeMail entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ActionChangeMailRepository extends JpaRepository<ActionChangeMail, Long> {
    @Query(
        "select actionChangeMail from ActionChangeMail actionChangeMail where actionChangeMail.user.login = ?#{principal.preferredUsername}"
    )
    List<ActionChangeMail> findByUserIsCurrentUser();
}

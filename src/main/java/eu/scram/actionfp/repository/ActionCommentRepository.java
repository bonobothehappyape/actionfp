package eu.scram.actionfp.repository;

import eu.scram.actionfp.domain.ActionComment;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the ActionComment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ActionCommentRepository extends JpaRepository<ActionComment, Long> {}

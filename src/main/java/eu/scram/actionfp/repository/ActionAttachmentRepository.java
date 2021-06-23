package eu.scram.actionfp.repository;

import eu.scram.actionfp.domain.ActionAttachment;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the ActionAttachment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ActionAttachmentRepository extends JpaRepository<ActionAttachment, Long> {}

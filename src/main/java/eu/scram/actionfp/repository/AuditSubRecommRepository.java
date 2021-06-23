package eu.scram.actionfp.repository;

import eu.scram.actionfp.domain.AuditSubRecomm;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the AuditSubRecomm entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AuditSubRecommRepository extends JpaRepository<AuditSubRecomm, Long> {}

package eu.scram.actionfp.repository;

import eu.scram.actionfp.domain.AuditRecomm;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the AuditRecomm entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AuditRecommRepository extends JpaRepository<AuditRecomm, Long> {}

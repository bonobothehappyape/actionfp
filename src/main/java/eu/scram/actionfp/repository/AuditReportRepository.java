package eu.scram.actionfp.repository;

import eu.scram.actionfp.domain.AuditReport;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the AuditReport entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AuditReportRepository extends JpaRepository<AuditReport, Long> {}

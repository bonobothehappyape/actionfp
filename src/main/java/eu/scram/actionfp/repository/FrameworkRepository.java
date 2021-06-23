package eu.scram.actionfp.repository;

import eu.scram.actionfp.domain.Framework;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Framework entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FrameworkRepository extends JpaRepository<Framework, Long> {}

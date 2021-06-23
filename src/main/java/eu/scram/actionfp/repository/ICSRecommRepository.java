package eu.scram.actionfp.repository;

import eu.scram.actionfp.domain.ICSRecomm;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the ICSRecomm entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ICSRecommRepository extends JpaRepository<ICSRecomm, Long> {}

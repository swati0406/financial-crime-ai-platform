package co.eaiwo.sanctions.repository;

import co.eaiwo.sanctions.domain.SanctionedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SanctionsRepository
        extends JpaRepository<SanctionedEntity, Long> {

    @Query("SELECT s FROM SanctionedEntity s WHERE " +
            "LOWER(s.entityName) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "AND s.status = 'ACTIVE'")
    List<SanctionedEntity> searchByName(@Param("name") String name);
}
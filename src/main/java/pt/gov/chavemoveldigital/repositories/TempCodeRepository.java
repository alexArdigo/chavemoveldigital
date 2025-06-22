package pt.gov.chavemoveldigital.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.gov.chavemoveldigital.entities.TempCode;

@Repository
public interface TempCodeRepository extends JpaRepository<TempCode, Long> {
    void deleteTempCodeById(Long id);
}

package pt.gov.chavemoveldigital.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.gov.chavemoveldigital.entities.TempCode;

public interface TempCodeRepository extends JpaRepository<TempCode, Long> {
    void deleteTempCodeById(Long id);
}

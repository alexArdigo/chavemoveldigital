package pt.gov.chavemoveldigital.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.gov.chavemoveldigital.entities.Municipality;

public interface MunicipalityRepository extends JpaRepository<Municipality, Long> {
}

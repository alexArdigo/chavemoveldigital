package pt.gov.chavemoveldigital.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.gov.chavemoveldigital.entities.District;
import pt.gov.chavemoveldigital.entities.Municipality;


public interface DistrictRepository extends JpaRepository<District, Long> {
}

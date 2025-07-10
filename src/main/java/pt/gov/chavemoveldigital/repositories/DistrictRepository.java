package pt.gov.chavemoveldigital.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.gov.chavemoveldigital.entities.District;

@Repository
public interface DistrictRepository extends JpaRepository<District, Long> {
}

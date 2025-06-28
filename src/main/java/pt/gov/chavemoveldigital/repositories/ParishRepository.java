package pt.gov.chavemoveldigital.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.gov.chavemoveldigital.entities.Parish;

import java.util.List;

public interface ParishRepository extends JpaRepository<Parish, Long> {
    List<Parish> findByName(String municipalityName);
}

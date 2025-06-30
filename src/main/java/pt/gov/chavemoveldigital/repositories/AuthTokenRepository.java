package pt.gov.chavemoveldigital.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.gov.chavemoveldigital.entities.AuthToken;

@Repository
public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {
}

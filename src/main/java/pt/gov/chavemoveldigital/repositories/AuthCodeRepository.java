package pt.gov.chavemoveldigital.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.gov.chavemoveldigital.entities.AuthCode;

@Repository
public interface AuthCodeRepository extends JpaRepository<AuthCode, Long> {

    AuthCode findAuthCodeByCodeAndClientId(String code, String clientId);
}

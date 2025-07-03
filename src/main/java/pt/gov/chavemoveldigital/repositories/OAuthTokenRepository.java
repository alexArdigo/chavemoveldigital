package pt.gov.chavemoveldigital.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.gov.chavemoveldigital.entities.OAuthToken;

@Repository
public interface OAuthTokenRepository extends JpaRepository<OAuthToken, Long> {
    OAuthToken findOAuthTokenByToken(String smsToken);
}

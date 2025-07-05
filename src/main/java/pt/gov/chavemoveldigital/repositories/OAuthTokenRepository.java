package pt.gov.chavemoveldigital.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pt.gov.chavemoveldigital.entities.OAuthToken;

@Repository
public interface OAuthTokenRepository extends JpaRepository<OAuthToken, Long> {
    OAuthToken findOAuthTokenByToken(String smsToken);

    @Modifying
    @Transactional
    @Query("DELETE FROM OAuthToken o WHERE o.token = :token")
    void deleteByToken(@Param("token") String token);

    boolean existsOAuthTokenByToken(String token);
}

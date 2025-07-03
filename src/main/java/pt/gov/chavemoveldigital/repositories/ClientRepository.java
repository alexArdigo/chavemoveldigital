package pt.gov.chavemoveldigital.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.gov.chavemoveldigital.entities.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {

    boolean existsClientByName(String euVoto);

    Client findClientByClientIdAndSecret(String clientId, String clientSecret);

    Client findClientByClientId(String clientId);
}

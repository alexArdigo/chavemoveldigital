package pt.gov.chavemoveldigital.services;


import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.gov.chavemoveldigital.entities.Client;
import pt.gov.chavemoveldigital.repositories.ClientRepository;

@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    private ClientRepository clientRepository;


    @PostConstruct
    public void init() {
        boolean existingEuVotoEntity = clientRepository.existsClientByName("EuVoto");

        if (!existingEuVotoEntity) {
            clientRepository.save(new Client("EuVoto", "euvoto_id", "euvoto_secret123"));
        }
    }
}

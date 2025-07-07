package pt.gov.chavemoveldigital.services;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.gov.chavemoveldigital.entities.Client;
import pt.gov.chavemoveldigital.entities.OAuthToken;
import pt.gov.chavemoveldigital.repositories.ClientRepository;
import pt.gov.chavemoveldigital.repositories.OAuthTokenRepository;

@Service
public class OAuthServiceImpl implements OAuthService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private OAuthTokenRepository oAuthTokenRepository;

    @Override
    public void saveToken(JsonNode payload) {
        String token = payload.get("token").asText();
        String clientId = payload.get("clientId").asText();
        String redirectUri = payload.get("redirectUri").asText();

        if (token == null || clientId == null || redirectUri == null)
            throw new IllegalArgumentException("Params cannot be null");

        Client client = clientRepository.findClientByClientId(clientId);

        oAuthTokenRepository.save(new OAuthToken(token, client, redirectUri));
    }

    @Override
    public boolean checkToken(String token) {
       return oAuthTokenRepository.existsOAuthTokenByToken(token);

    }
}

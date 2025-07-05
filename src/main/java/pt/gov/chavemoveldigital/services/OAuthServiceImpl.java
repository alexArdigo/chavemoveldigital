package pt.gov.chavemoveldigital.services;

import ch.qos.logback.core.subst.Token;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pt.gov.chavemoveldigital.entities.AuthCode;
import pt.gov.chavemoveldigital.entities.Client;
import pt.gov.chavemoveldigital.entities.OAuthToken;
import pt.gov.chavemoveldigital.entities.User;
import pt.gov.chavemoveldigital.repositories.AuthCodeRepository;
import pt.gov.chavemoveldigital.repositories.ClientRepository;
import pt.gov.chavemoveldigital.repositories.OAuthTokenRepository;
import pt.gov.chavemoveldigital.repositories.UserRepository;
import pt.gov.chavemoveldigital.security.UserAuthenticationProvider;

import java.util.Map;

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
}

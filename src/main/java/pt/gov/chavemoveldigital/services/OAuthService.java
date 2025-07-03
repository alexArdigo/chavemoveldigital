package pt.gov.chavemoveldigital.services;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

public interface OAuthService {

    Map<String, Object> token(String clientId, String clientSecret, String userId);

    void saveClientToken(JsonNode payload);
}

package pt.gov.chavemoveldigital.services;

import com.fasterxml.jackson.databind.JsonNode;

public interface OAuthService {

    void saveToken(JsonNode payload);

    boolean checkToken(String token);
}

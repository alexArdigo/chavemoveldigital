package pt.gov.chavemoveldigital.services;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

public interface OAuthService {
    String authorize(
            String response_type,
            String client_id,
            String redirect_uri,
            HttpSession session
    );

    Map<String, Object> token(String code, String client_id, String client_secret);

    Map<String, Object> validateAuthCodeAndGetUser(String code, String client_id);
}

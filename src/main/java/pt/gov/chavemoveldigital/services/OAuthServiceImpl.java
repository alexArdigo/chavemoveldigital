package pt.gov.chavemoveldigital.services;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.gov.chavemoveldigital.entities.AuthCode;
import pt.gov.chavemoveldigital.entities.AuthToken;
import pt.gov.chavemoveldigital.entities.User;
import pt.gov.chavemoveldigital.repositories.AuthCodeRepository;
import pt.gov.chavemoveldigital.repositories.AuthTokenRepository;
import pt.gov.chavemoveldigital.repositories.UserRepository;

import java.util.Map;
import java.util.UUID;

@Service
public class OAuthServiceImpl implements OAuthService {

    @Autowired
    private AuthCodeRepository authCodeRepository;

    @Autowired
    private AuthTokenRepository authTokenRepository;

    @Autowired
    private UserRepository userRepository;

    public OAuthServiceImpl(AuthCodeRepository authCodeRepository) {
        this.authCodeRepository = authCodeRepository;
    }

    @Override
    public String authorize(String response_type, String client_id, String redirect_uri, HttpSession session) {

        session.setAttribute("client_id", client_id);
        session.setAttribute("redirect_uri", redirect_uri);
        session.setAttribute("response_type", response_type);

        if (session.getAttribute("user") != null) {
            String code = UUID.randomUUID().toString();

            Map<String, Object> authCodeAndUser = validateAuthCodeAndGetUser(code, client_id);
            AuthCode authCode = (AuthCode) authCodeAndUser.get("authCode");
            User user = (User) authCodeAndUser.get("user");

            authCodeRepository.save(new AuthCode(code, client_id, user.getId()));
            authCodeRepository.delete(authCode);
            return redirect_uri + "?code=" + code;
        }

        return "http://localhost:5174/authorization";
    }

    @Override
    public Map<String, Object> token(String code, String client_id, String client_secret) {
        try {

            Map<String, Object> authCodeAndUser = validateAuthCodeAndGetUser(code, client_id);
            User user = (User) authCodeAndUser.get("user");

            AuthToken token = new AuthToken(UUID.randomUUID().toString(), client_id);
            authTokenRepository.save(token);

            return Map.of(
                    "access_token", token,
                    "token_type", "Bearer",
                    "expires_in", 3600,
                    "user", user
            );

        } catch (Exception e) {
            System.err.println("Failed to send token to client backend: " + e.getMessage());
            return Map.of("error", "Failed to complete authentication");

        }
    }

    @Override
    public Map<String, Object> validateAuthCodeAndGetUser(String code, String client_id) {
        AuthCode authCode = authCodeRepository.findAuthCodeByCodeAndClientId(code, client_id);
        if (authCode == null || authCode.getCode() == null || authCode.getClientId() == null) {
            throw new IllegalArgumentException("Invalid authorization code or client ID");
        }
        User user = userRepository.findById(authCode.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return Map.of("authCode", authCode, "user", user);
    }


}

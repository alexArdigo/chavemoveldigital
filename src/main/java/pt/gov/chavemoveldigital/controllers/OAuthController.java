package pt.gov.chavemoveldigital.controllers;


import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import pt.gov.chavemoveldigital.entities.AuthCode;
import pt.gov.chavemoveldigital.entities.AuthToken;
import pt.gov.chavemoveldigital.entities.User;
import pt.gov.chavemoveldigital.repositories.AuthCodeRepository;
import pt.gov.chavemoveldigital.repositories.AuthTokenRepository;
import pt.gov.chavemoveldigital.repositories.UserRepository;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/oauth")
public class OAuthController {

    @Autowired
    private AuthCodeRepository authCodeRepository;

    @Autowired
    private AuthTokenRepository authTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/authorize")
    public RedirectView authorize(
            @RequestParam String response_type,
            @RequestParam String client_id,
            @RequestParam String redirect_uri,
            HttpSession session
    ) {
        System.out.println(" Authorize endpoint called");
        session.setAttribute("client_id", client_id);
        session.setAttribute("redirect_uri", redirect_uri);
        session.setAttribute("response_type", response_type);

       /* if (session.getAttribute("user") != null) {
            String code = UUID.randomUUID().toString();
            authCodeRepository.save(new AuthCode(code, client_id));
            return new RedirectView(redirect_uri + "?code=" + code);
        }*/

        return new RedirectView("http://localhost:5174/authorization");
    }

    @PostMapping("/token")
    public ResponseEntity<?> token(
            @RequestParam String code,
            @RequestParam String client_id,
            @RequestParam String client_secret
    ) {
        try {
            AuthCode authCode = authCodeRepository.findAuthCodeByCodeAndClientId(code, client_id);

            if (authCode == null || authCode.getCode() == null || authCode.getClientId() == null) {
                return ResponseEntity.badRequest().body("Invalid code");
            }

            AuthToken token = new AuthToken(UUID.randomUUID().toString(), client_id);
            authTokenRepository.save(token);

            User user = userRepository.findById(authCode.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            authCodeRepository.delete(authCode);

            return ResponseEntity.ok(Map.of(
                    "access_token", token,
                    "token_type", "Bearer",
                    "expires_in", 3600,
                    "user", user
            ));

        } catch (Exception e) {
            System.err.println("Failed to send token to client backend: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to complete authentication"));

        }
    }

}

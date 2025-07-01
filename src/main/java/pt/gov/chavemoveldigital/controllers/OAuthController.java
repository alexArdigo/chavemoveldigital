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
import pt.gov.chavemoveldigital.services.OAuthService;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/oauth")
public class OAuthController {

    @Autowired
    private OAuthService oAuthService;

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
        return new RedirectView(
                oAuthService.authorize(
                        response_type,
                        client_id,
                        redirect_uri,
                        session
                )
        );
    }

    @PostMapping("/token")
    public ResponseEntity<?> token(
            @RequestParam String code,
            @RequestParam String client_id,
            @RequestParam String client_secret
    ) {
        return ResponseEntity.ok().body(
                oAuthService.token(
                        code,
                        client_id,
                        client_secret
                )
        );
    }

}

package pt.gov.chavemoveldigital.controllers;


import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import pt.gov.chavemoveldigital.services.OAuthService;

@RestController
@RequestMapping("/oauth")
public class OAuthController {

    @Autowired
    private OAuthService oAuthService;


    @PostMapping("/client-token")
    public ResponseEntity<?> saveClientToken(
            @RequestBody JsonNode payload) {
        oAuthService.saveClientToken(payload);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/token")
    public ResponseEntity<?> token(
            @RequestParam("client_id") String clientId,
            @RequestParam("client_secret") String clientSecret,
            @RequestParam("user_id") String userId
    ) {
        return ResponseEntity.ok().body(oAuthService.token(clientId, clientSecret, userId));
    }

}

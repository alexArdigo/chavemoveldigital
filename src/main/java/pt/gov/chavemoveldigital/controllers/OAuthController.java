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

    @PostMapping("/token")
    public ResponseEntity<?> saveToken(@RequestBody JsonNode payload) {
        oAuthService.saveToken(payload);
        return ResponseEntity.ok().build();
    }

}

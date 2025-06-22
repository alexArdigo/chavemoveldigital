package pt.gov.chavemoveldigital.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.gov.chavemoveldigital.services.AuthService;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/authentication")
    public ResponseEntity<?> authenticate(String telephoneNumber) {
        return ResponseEntity.ok().body(authService.authenticate(telephoneNumber));
    }

    @PostMapping("/authentication/code")
    public ResponseEntity<?> insertCode(Long code) {
        return ResponseEntity.ok().body(authService.insertCode(code));
    }
}

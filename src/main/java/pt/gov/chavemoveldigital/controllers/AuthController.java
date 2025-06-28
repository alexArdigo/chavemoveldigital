package pt.gov.chavemoveldigital.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import pt.gov.chavemoveldigital.models.UserDTO;
import pt.gov.chavemoveldigital.services.AuthService;


@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(UserDTO userDTO) {
        return ResponseEntity.ok().body(authService.authenticate(userDTO));
    }

    @PostMapping("/authenticate/code")
    public ResponseEntity<?> insertCode(Integer code) {
        return ResponseEntity.ok().body(authService.insertCode(code));
    }


}

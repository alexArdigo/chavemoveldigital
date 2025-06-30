package pt.gov.chavemoveldigital.controllers;


import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import pt.gov.chavemoveldigital.services.AuthService;


@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(
            @RequestParam String telephoneNumber,
            @RequestParam Integer pin,
            HttpSession session
    ) {
        return ResponseEntity.ok(authService.authenticate(telephoneNumber, pin, session));
    }

    @PostMapping("/authenticate/code")
    public RedirectView verifyCode(@RequestParam Integer code, HttpSession session) {
        System.out.println("code = " + code);
        return authService.verifyCode(code, session);
    }


}

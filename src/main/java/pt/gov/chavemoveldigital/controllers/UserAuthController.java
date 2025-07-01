package pt.gov.chavemoveldigital.controllers;


import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import pt.gov.chavemoveldigital.services.UserAuthService;


@Controller
@RequestMapping("/user")
public class UserAuthController {

    @Autowired
    private UserAuthService userAuthService;

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(
            @RequestParam String telephoneNumber,
            @RequestParam Integer pin,
            HttpSession session
    ) {
        return ResponseEntity.ok(userAuthService.authenticate(telephoneNumber, pin, session));
    }

    @PostMapping("/verify-smscode")
    public RedirectView verifySMSCode(@RequestParam Integer code, HttpSession session) {
        return userAuthService.verifySMSCode(code, session);
    }


}

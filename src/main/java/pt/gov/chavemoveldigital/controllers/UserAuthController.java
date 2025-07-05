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
@RequestMapping("/users")
public class UserAuthController {

    @Autowired
    private UserAuthService userAuthService;

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(
            @RequestParam String telephoneNumber,
            @RequestParam Integer pin
    ) {
        return ResponseEntity.ok(userAuthService.authenticate(telephoneNumber, pin));
    }

    @PostMapping("/verify-smscode")
    public ResponseEntity<?> verifySMSCode(
            @RequestParam Integer SMSCode,
            @RequestParam String token
    ) {
        System.out.println("SMSCode = " + SMSCode);
        System.out.println("token = " + token);
        return userAuthService.verifySMSCode(SMSCode, token);
    }

}

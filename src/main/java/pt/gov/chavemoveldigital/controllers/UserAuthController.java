package pt.gov.chavemoveldigital.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pt.gov.chavemoveldigital.services.UserAuthService;


@Controller
@RequestMapping("/users")
public class UserAuthController {

    @Autowired
    private UserAuthService userAuthService;

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(
            @RequestParam String telephoneNumber,
            @RequestParam Integer pin,
            @RequestParam String token
    ) {
        return ResponseEntity.ok(userAuthService.authenticate(telephoneNumber, pin, token));
    }

    @GetMapping("/sms-code-time-left")
    public ResponseEntity<?> SMSCodeTimeLeft(@RequestParam String token) {
        return ResponseEntity.ok().body(userAuthService.SMSCodeTimeLeft(token));
    }

    @PostMapping("/verify-smscode")
    public ResponseEntity<?> verifySMSCode(
            @RequestParam Integer SMSCode,
            @RequestParam String token
    ) {
        return userAuthService.verifySMSCode(SMSCode, token);
    }

}

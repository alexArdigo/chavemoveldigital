package pt.gov.chavemoveldigital.services;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.view.RedirectView;
import pt.gov.chavemoveldigital.entities.SMSCode;
import pt.gov.chavemoveldigital.entities.User;

import java.util.Map;


public interface UserAuthService {
    Map<String, Object> authenticate(String telephoneNumber, Integer pin);

    User validateUser(String telephoneNumber, Integer pin);

    String verifySMSCode(Integer SMSCode, String SMSToken);

    void setTimeout(SMSCode code);

    String callbackClient(String redirectUri, String cliendToken, Long userId);
}

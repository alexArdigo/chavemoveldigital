package pt.gov.chavemoveldigital.services;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.view.RedirectView;
import pt.gov.chavemoveldigital.entities.SMSCode;
import pt.gov.chavemoveldigital.entities.User;

import java.util.Map;


public interface UserAuthService {
    Map<String, Object> authenticate(String telephoneNumber, Integer pin, HttpSession session);

    User validateUser(String telephoneNumber, Integer pin);

    RedirectView verifySMSCode(Integer code, HttpSession session);

    void setTimeout(SMSCode code);

    Integer generateCode();
}

package pt.gov.chavemoveldigital.services;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.view.RedirectView;
import pt.gov.chavemoveldigital.entities.SMSCode;

import java.util.Map;


public interface AuthService {
    Map<String, Object> authenticate(String telephoneNumber, Integer pin, HttpSession session);

    void verifyUser(String telephoneNumber, Integer pin);

    RedirectView verifyCode(Integer code, HttpSession session);

    boolean verifySmsCode(Integer code, String telephone);

    void setTimeout(SMSCode code);

    Integer generateCode();
}

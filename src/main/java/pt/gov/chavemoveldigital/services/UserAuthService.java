package pt.gov.chavemoveldigital.services;

import org.springframework.http.ResponseEntity;
import pt.gov.chavemoveldigital.entities.SMSCode;
import pt.gov.chavemoveldigital.entities.User;

import java.util.Map;


public interface UserAuthService {
    Map<String, Object> authenticate(String telephoneNumber, Integer pin);

    void validateUser(String telephoneNumber, Integer pin);

    ResponseEntity<?> verifySMSCode(Integer SMSCode, String SMSToken);

    void setTimeout(SMSCode code);

    Long callback(String redirectUri, String cliendToken, User user);
}

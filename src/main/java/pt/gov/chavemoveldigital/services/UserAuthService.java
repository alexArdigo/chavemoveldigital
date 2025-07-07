package pt.gov.chavemoveldigital.services;

import org.springframework.http.ResponseEntity;
import pt.gov.chavemoveldigital.entities.User;

import java.util.Map;


public interface UserAuthService {
    Map<String, Object> authenticate(String telephoneNumber, Integer pin, String token);

    void validateUser(String telephoneNumber, Integer pin);

    ResponseEntity<?> verifySMSCode(Integer SMSCode, String SMSToken);

    int SMSCodeTimeLeft(String token);

    Long callback(String redirectUri, String token, User user);
}

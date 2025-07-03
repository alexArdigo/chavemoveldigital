package pt.gov.chavemoveldigital.services;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;
import pt.gov.chavemoveldigital.entities.OAuthToken;
import pt.gov.chavemoveldigital.entities.SMSCode;
import pt.gov.chavemoveldigital.entities.User;
import pt.gov.chavemoveldigital.repositories.OAuthTokenRepository;
import pt.gov.chavemoveldigital.repositories.SMSCodeRepository;
import pt.gov.chavemoveldigital.repositories.UserRepository;

import java.util.Map;

@Service
public class UserAuthServiceImpl implements UserAuthService {


    private final int delay = 600;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SMSCodeRepository SMSCodeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SMSCodeDeletionService SMSCodeDeletionService;


    @Autowired
    private OAuthTokenRepository oAuthTokenRepository;


    @Override
    public Map<String, Object> authenticate(String telephoneNumber, Integer pin) {

        validateUser(telephoneNumber, pin);

        SMSCode previousCode = SMSCodeRepository.findSMSCodeByTelephoneNumber(telephoneNumber);
        if (previousCode != null) {
            SMSCodeRepository.delete(previousCode);
        }

        SMSCode code = new SMSCode(telephoneNumber, pin);
        SMSCodeRepository.save(code);
        setTimeout(code);

        return Map.of(
                "next", "/code-validation",
                "params", Map.of("SMScode", code.getCode(), "delay", delay)
        );
    }

    @Override
    public String verifySMSCode(Integer SMSCode, String clientToken) {

        SMSCode existingSMSCode = SMSCodeRepository.findSMSCodeByCode(SMSCode);
        if (existingSMSCode == null || existingSMSCode.getId() == null) {
            throw new NullPointerException("SMS code not found or expired");
        }

        User user = userRepository.findUserByTelephoneNumber(existingSMSCode.getTelephoneNumber());
        if (user == null || user.getId() == null) {
            throw new NullPointerException("User not found");
        }

        OAuthToken oAuthToken = oAuthTokenRepository.findOAuthTokenByToken(clientToken);
        if (oAuthToken == null || oAuthToken.getToken() == null)
            throw new NullPointerException("Token not found");

        String redirectUriFrontendClientSide = callbackClient(
                oAuthToken.getRedirectUri(),
                oAuthToken.getToken(),
                user.getId()
        );

        System.out.println("redirectUriFrontendClientSide = " + redirectUriFrontendClientSide);

        return redirectUriFrontendClientSide;

    }

    @Override
    public User validateUser(String telephoneNumber, Integer pin) {

        User existingUser = userRepository.findUserByTelephoneNumber(telephoneNumber);
        if (existingUser == null || existingUser.getNif() == null)
            throw new NullPointerException("Incorrect data");

        if (!passwordEncoder.matches(pin.toString(), existingUser.getPin()))
            throw new NullPointerException("Incorrect data");

        return existingUser;
    }

    @Override
    public void setTimeout(SMSCode code) {
        SMSCodeDeletionService.deleteTempCodeAfterDelay(code.getId(), delay * 1000);
    }

    @Override
    public String callbackClient(String redirectUri, String cliendToken, Long userId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject tokenRequest = new JSONObject();
        tokenRequest.put("clientToken", cliendToken);
        tokenRequest.put("userId", userId);

        String response = restTemplate.postForObject(redirectUri, new HttpEntity<>(tokenRequest.toString(), headers), String.class);

        if (response == null || response.isEmpty()) {
            throw new RuntimeException("Failed to redirect to client side");
        }

        return response;
    }
}

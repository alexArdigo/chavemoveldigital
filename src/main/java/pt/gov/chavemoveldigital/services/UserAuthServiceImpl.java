package pt.gov.chavemoveldigital.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pt.gov.chavemoveldigital.entities.OAuthToken;
import pt.gov.chavemoveldigital.entities.SMSCode;
import pt.gov.chavemoveldigital.entities.User;
import pt.gov.chavemoveldigital.repositories.OAuthTokenRepository;
import pt.gov.chavemoveldigital.repositories.SMSCodeRepository;
import pt.gov.chavemoveldigital.repositories.UserRepository;

import java.time.ZoneId;
import java.util.Map;

@Service
public class UserAuthServiceImpl implements UserAuthService {

    private final int initialSMSCodeTimerInMilliseconds = 60000; // 60 seconds

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SMSCodeRepository SMSCodeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    private OAuthTokenRepository oAuthTokenRepository;


    @Override
    public Map<String, Object> authenticate(String telephoneNumber, Integer pin, String token) {

        validateUser(telephoneNumber, pin);

        SMSCode previousCode = SMSCodeRepository.findSMSCodeByTelephoneNumber(telephoneNumber);
        if (previousCode != null) {
            SMSCodeRepository.delete(previousCode);
        }

        SMSCode code = new SMSCode(telephoneNumber, token);
        SMSCodeRepository.save(code);

        return Map.of(
                "next", "/code-validation",
                "params", Map.of("SMScode", code.getCode(), "timer", initialSMSCodeTimerInMilliseconds / 1000)
        );
    }

    @Override
    public ResponseEntity<?> verifySMSCode(Integer smsCode, String token) {

        SMSCode existingSMSCode = SMSCodeRepository.findSMSCodeByCode(smsCode);
        if (existingSMSCode == null || existingSMSCode.getId() == null) {
            throw new NullPointerException("SMS code not found");
        }

        User existingUser = userRepository.findUserByTelephoneNumber(existingSMSCode.getTelephoneNumber());
        if (existingUser == null || existingUser.getId() == null) {
            throw new NullPointerException("User not found");
        }

        OAuthToken oAuthToken = oAuthTokenRepository.findOAuthTokenByToken(token);
        if (oAuthToken == null || oAuthToken.getToken() == null)
            throw new NullPointerException("Token not found");

        return ResponseEntity.ok().body(
                callback(
                        oAuthToken.getRedirectUri(),
                        oAuthToken.getToken(),
                        existingUser
                )
        );
    }

    @Override
    public Long callback(String redirectUri, String token, User user) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject tokenRequest = new JSONObject();
        tokenRequest.put("token", token);

        ObjectMapper objectMapper = new ObjectMapper();
        String userJson;
        try {
            userJson = objectMapper.writeValueAsString(user);
        } catch (Exception e) {
            throw new RuntimeException("Error converting user to JSON: " + e.getMessage(), e);
        }
        tokenRequest.put("user", new JSONObject(userJson));

        ResponseEntity<Long> response = restTemplate.postForEntity(redirectUri, new HttpEntity<>(tokenRequest.toString(), headers), Long.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to redirect to client side");
        }

        oAuthTokenRepository.deleteByToken(token);
        return response.getBody();
    }

    @Override
    public void validateUser(String telephoneNumber, Integer pin) {

        User existingUser = userRepository.findUserByTelephoneNumber(telephoneNumber);
        if (existingUser == null || existingUser.getNif() == null)
            throw new NullPointerException("Incorrect data");

        if (!passwordEncoder.matches(pin.toString(), existingUser.getPin()))
            throw new NullPointerException("Incorrect data");

    }

    @Override
    public int SMSCodeTimeLeft(String token) {

        SMSCode existingCode = SMSCodeRepository.findSMSCodeByToken(token);

        if (existingCode == null || existingCode.getId() == null) {
            throw new NullPointerException("SMS code not found");
        }

        long currentTimeInSeconds = System.currentTimeMillis() / 1000;
        long codeCreationTimeInSeconds = existingCode.getTimestamp().atZone(ZoneId.systemDefault()).toEpochSecond();

        long elapsedTimeInSeconds = currentTimeInSeconds - codeCreationTimeInSeconds;

        long timeLeftInSeconds = (initialSMSCodeTimerInMilliseconds / 1000) - elapsedTimeInSeconds;

        return (int) Math.max(0, timeLeftInSeconds);
    }
}

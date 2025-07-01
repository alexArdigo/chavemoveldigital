package pt.gov.chavemoveldigital.services;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;
import pt.gov.chavemoveldigital.entities.AuthCode;
import pt.gov.chavemoveldigital.entities.SMSCode;
import pt.gov.chavemoveldigital.entities.User;
import pt.gov.chavemoveldigital.repositories.AuthCodeRepository;
import pt.gov.chavemoveldigital.repositories.SMSCodeRepository;
import pt.gov.chavemoveldigital.repositories.UserRepository;

import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    int delay = 600;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SMSCodeRepository SMSCodeRepository;

    @Autowired
    private AuthCodeRepository authCodeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TempCodeDeletionService tempCodeDeletionService;


    @Override
    public Map<String, Object> authenticate(String telephoneNumber, Integer pin, HttpSession session) {

        verifyUser(telephoneNumber, pin);

        SMSCode previousCode = SMSCodeRepository.findSMSCodeByTelephoneNumber(telephoneNumber);
        if (previousCode != null) {
            SMSCodeRepository.delete(previousCode);
        }

        SMSCode code = new SMSCode(telephoneNumber, generateCode());
        SMSCodeRepository.save(code);
        setTimeout(code);

        session.setAttribute("pendingUser", telephoneNumber);

        return Map.of(
                "next", "/code-validation",
                "params", Map.of("SMScode", code.getCode(), "delay", delay)
        );
    }

    @Override
    public RedirectView verifyCode(Integer code, HttpSession session) {
        String telephone = session.getAttribute("pendingUser").toString();

        SMSCode existingSMSCode = SMSCodeRepository.findSMSCodeByCode(code);
        if (existingSMSCode == null || existingSMSCode.getId() == null) {
            return new RedirectView("/error?message=invalid_code");
        }

        User user = userRepository.findUserByTelephoneNumber(telephone);

        if (user == null || user.getNif() == null) {
            return new RedirectView("/error?message=invalid_user");
        }

        // Authenticated
        session.setAttribute("user", telephone);

        String clientId = session.getAttribute("client_id").toString();
        String redirectUri = session.getAttribute("redirect_uri").toString();

        String authCode = UUID.randomUUID().toString();
        authCodeRepository.save(new AuthCode(authCode, clientId, user.getId()));

        return new RedirectView(redirectUri + "?code=" + authCode);
    }

    @Override
    public void verifyUser(String telephoneNumber, Integer pin) {
        User existingUser = userRepository.findUserByTelephoneNumber(telephoneNumber);
        if (existingUser == null || existingUser.getNif() == null)
            throw new NullPointerException("Incorrect data");

        if (!passwordEncoder.matches(pin.toString(), existingUser.getPin()))
            throw new NullPointerException("Incorrect data");
    }

    @Override
    public boolean verifySmsCode(Integer code, String telephone) {
        return SMSCodeRepository.existsSMSCodeByCodeAndTelephoneNumber(code, telephone);
    }

    @Override
    public void setTimeout(SMSCode code) {
        tempCodeDeletionService.deleteTempCodeAfterDelay(code.getId(), delay * 1000);
    }

    @Override
    public Integer generateCode() {
        Random random = new Random();
        int min = 100000;
        int max = 999999;
        return random.nextInt(max - min) + min;
    }
}

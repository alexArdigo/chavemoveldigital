package pt.gov.chavemoveldigital.services;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pt.gov.chavemoveldigital.entities.TempCode;
import pt.gov.chavemoveldigital.entities.User;
import pt.gov.chavemoveldigital.models.UserDTO;
import pt.gov.chavemoveldigital.repositories.TempCodeRepository;
import pt.gov.chavemoveldigital.repositories.UserRepository;

import java.util.Random;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TempCodeRepository tempCodeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TempCodeDeletionService tempCodeDeletionService;


    @Override
    public TempCode authenticate(UserDTO userDTO) {
        User existingUser = userRepository.findUserByTelephoneNumber(userDTO.getTelephoneNumber());
        if (existingUser == null || existingUser.getNif() == null)
            throw new NullPointerException("Incorrect data");

        if (!passwordEncoder.matches(userDTO.getPin().toString(), existingUser.getPin()))
            throw new NullPointerException("Incorrect data");

        TempCode previousCode = tempCodeRepository.findByUser(existingUser);
        if (previousCode != null) {
            tempCodeRepository.delete(previousCode);
        }

        TempCode code = new TempCode(existingUser, generateCode());
        tempCodeRepository.save(code);
        setTimeout(code);

        return code;
    }


    @Override
    public User insertCode(Integer code) {
        TempCode existingTempCode = tempCodeRepository.findTempCodeByCode(code);
        if (existingTempCode == null || existingTempCode.getId() == null) {
            throw new NullPointerException("Incorrect code");
        }
        User user = existingTempCode.getUser();
        tempCodeRepository.delete(existingTempCode);
        return user;
    }

    @Override
    public void setTimeout(TempCode code) {
        int delay = 60000;
        tempCodeDeletionService.deleteTempCodeAfterDelay(code.getId(), delay);
    }

    public Integer generateCode() {
        Random random = new Random();
        int min = 100000;
        int max = 999999;
        return random.nextInt(max - min) + min;
    }
}

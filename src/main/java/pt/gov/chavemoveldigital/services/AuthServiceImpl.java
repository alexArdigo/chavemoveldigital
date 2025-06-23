package pt.gov.chavemoveldigital.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.gov.chavemoveldigital.entities.TempCode;
import pt.gov.chavemoveldigital.entities.User;
import pt.gov.chavemoveldigital.models.UserDTO;
import pt.gov.chavemoveldigital.repositories.TempCodeRepository;
import pt.gov.chavemoveldigital.repositories.UserRepository;

import java.util.Random;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TempCodeRepository tempCodeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TempCodeDeletionService tempCodeDeletionService;


    @Override
    public Object authenticate(UserDTO userDTO) {
        User existingUser = userRepository.findUserByTelephoneNumber(userDTO.getTelephoneNumber());
        if (existingUser == null || existingUser.getNif() == null)
            throw new NullPointerException("Incorrect data");

        String pinEncoded = passwordEncoder.encode(userDTO.getPin().toString());
        if (!passwordEncoder.matches(existingUser.getPin().toString(), pinEncoded))
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
    public Object insertCode(Long code) {
        return null;
    }

    @Override
    public void setTimeout(TempCode code) {
        int delay = 10000;
        tempCodeDeletionService.deleteTempCodeAfterDelay(code.getId(), delay);
    }

    public Integer generateCode() {
        Random random = new Random();
        int min = 100000;
        int max = 999999;
        return random.nextInt(max - min) + min;
    }
}

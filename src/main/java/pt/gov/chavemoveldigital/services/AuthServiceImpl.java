package pt.gov.chavemoveldigital.services;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pt.gov.chavemoveldigital.entities.TempCode;
import pt.gov.chavemoveldigital.entities.User;
import pt.gov.chavemoveldigital.models.CodeDTO;
import pt.gov.chavemoveldigital.models.UserDTO;
import pt.gov.chavemoveldigital.repositories.TempCodeRepository;
import pt.gov.chavemoveldigital.repositories.UserRepository;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Random;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    int delay = 60000;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TempCodeRepository tempCodeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TempCodeDeletionService tempCodeDeletionService;


    @Override
    public CodeDTO authenticate(UserDTO userDTO) {
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

        return new CodeDTO(code.getCode(), delay);
    }

    @Override
    public User insertCode(Integer code) {
        TempCode existingTempCode = tempCodeRepository.findTempCodeByCode(code);
        if (existingTempCode == null || existingTempCode.getId() == null) {
            throw new NullPointerException("Incorrect code");
        }
        User user = existingTempCode.getUser();
        tempCodeRepository.delete(existingTempCode);
        apiClient(user);
        return user;
    }

    @Override
    public void setTimeout(TempCode code) {
        tempCodeDeletionService.deleteTempCodeAfterDelay(code.getId(), delay);
    }

    @Override
    public Integer generateCode() {
        Random random = new Random();
        int min = 100000;
        int max = 999999;
        return random.nextInt(max - min) + min;
    }


    @Override
    public void apiClient(User user) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject userRequest = getJsonObject(user);

        System.out.println("userRequest = " + userRequest);
        String requestUrl = "http://localhost:8080/voters/authenticated";
        restTemplate.postForObject(requestUrl, new HttpEntity<>(userRequest.toString(), headers), UserDTO.class);

    }

    private static JSONObject getJsonObject(User user) {
        JSONObject userRequest = new JSONObject();
        userRequest.put("nif", user.getNif());
        userRequest.put("telephoneNumber", user.getTelephoneNumber());
        userRequest.put("id", user.getId());
        userRequest.put("firstName", user.getFirstName());
        userRequest.put("lastName", user.getLastName());
        userRequest.put("district", user.getDistrict().getValue());
        userRequest.put("municipality", user.getMunicipality().getValue());
        userRequest.put("parish", user.getParish());
        return userRequest;
    }
}

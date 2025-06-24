package pt.gov.chavemoveldigital.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pt.gov.chavemoveldigital.entities.User;
import pt.gov.chavemoveldigital.models.UsersDTO;
import pt.gov.chavemoveldigital.repositories.UserRepository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        System.out.println("USERS");
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            List<UsersDTO> usersDTO = objectMapper
                    .readValue(
                            new File("src/main/java/pt/gov/chavemoveldigital/json/users.json"),
                            new TypeReference<List<UsersDTO>>() {
                            }
                    );

            List<User> users = new ArrayList<>();
            for (UsersDTO userDTO : usersDTO) {
                User existingUser = userRepository.findUserByNif(userDTO.getNif());
                if (existingUser == null || existingUser.getNif() == null) {
                    String pin = passwordEncoder.encode(userDTO.getPin().toString());
                    User user = new User(userDTO, pin);
                    users.add(user);
                }
            }
            userRepository.saveAll(users);
            System.out.println("Users added to DB");
        } catch (RuntimeException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}

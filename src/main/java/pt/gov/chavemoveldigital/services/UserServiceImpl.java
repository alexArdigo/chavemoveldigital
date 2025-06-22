package pt.gov.chavemoveldigital.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    public void init() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            List<UsersDTO> usersDTO = objectMapper
                    .readValue(
                            new File("src/main/java/com/example/eventsmanagement/json/artists.json"),
                            new TypeReference<List<UsersDTO>>() {
                            }
                    );

            List<User> users = new ArrayList<>();
            for (UsersDTO userDTO : usersDTO) {
                User existingUser = userRepository.findUserByNif(usersDTO.getNif());
                if (existingUser == null || existingUser.getId() == null) {
                    User user = new User(usersDTO);
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

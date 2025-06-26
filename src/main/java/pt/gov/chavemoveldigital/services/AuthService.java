package pt.gov.chavemoveldigital.services;

import pt.gov.chavemoveldigital.entities.TempCode;
import pt.gov.chavemoveldigital.entities.User;
import pt.gov.chavemoveldigital.models.CodeDTO;
import pt.gov.chavemoveldigital.models.UserDTO;


public interface AuthService {
    CodeDTO authenticate(UserDTO userDTO);

    Object insertCode(Integer code);

    void setTimeout(TempCode code);

    Integer generateCode();

    void apiClient(User user);
}

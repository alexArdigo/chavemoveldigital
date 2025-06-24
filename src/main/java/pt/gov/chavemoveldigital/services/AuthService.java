package pt.gov.chavemoveldigital.services;

import pt.gov.chavemoveldigital.entities.TempCode;
import pt.gov.chavemoveldigital.models.UserDTO;


public interface AuthService {
    TempCode authenticate(UserDTO userDTO);

    Object insertCode(Integer code);

    void setTimeout(TempCode code);

    Integer generateCode();
}

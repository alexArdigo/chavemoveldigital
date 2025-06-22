package pt.gov.chavemoveldigital.services;

import pt.gov.chavemoveldigital.entities.TempCode;
import pt.gov.chavemoveldigital.models.UserDTO;

public interface AuthService {
    Object authenticate(UserDTO userDTO);

    Object insertCode(Long code);

    void setTimeout(TempCode code);
}

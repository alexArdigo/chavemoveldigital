package pt.gov.chavemoveldigital.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.gov.chavemoveldigital.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByTelephoneNumber(String telephoneNumber);

}

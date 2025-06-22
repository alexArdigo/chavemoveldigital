package pt.gov.chavemoveldigital.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.gov.chavemoveldigital.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByTelephoneNumber(String telephoneNumber);

}

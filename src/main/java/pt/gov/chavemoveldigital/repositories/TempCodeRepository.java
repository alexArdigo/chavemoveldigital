package pt.gov.chavemoveldigital.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.gov.chavemoveldigital.entities.TempCode;
import pt.gov.chavemoveldigital.entities.User;

@Repository
public interface TempCodeRepository extends JpaRepository<TempCode, Long> {
    void deleteTempCodeById(Long id);

    TempCode findByUser(User existingUser);

    TempCode findTempCodeByCode(Integer code);
}

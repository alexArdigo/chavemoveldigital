package pt.gov.chavemoveldigital.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.gov.chavemoveldigital.entities.SMSCode;
import pt.gov.chavemoveldigital.entities.User;

@Repository
public interface SMSCodeRepository extends JpaRepository<SMSCode, Long> {
    void deleteTempCodeById(Long id);

    SMSCode findSMSCodeByCode(Integer code);

    boolean existsSMSCodeByCodeAndTelephoneNumber(Integer code, String telephoneNumber);

    SMSCode findSMSCodeByTelephoneNumber(String telephoneNumber);
}

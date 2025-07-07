package pt.gov.chavemoveldigital.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.gov.chavemoveldigital.entities.SMSCode;

@Repository
public interface SMSCodeRepository extends JpaRepository<SMSCode, Long> {
    SMSCode findSMSCodeByCode(Integer code);

    SMSCode findSMSCodeByTelephoneNumber(String telephoneNumber);

    SMSCode findSMSCodeByToken(String token);
}

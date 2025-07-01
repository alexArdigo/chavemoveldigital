package pt.gov.chavemoveldigital.services;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.gov.chavemoveldigital.repositories.SMSCodeRepository;

@Service
public class SMSCodeDeletionService {

    private final SMSCodeRepository SMSCodeRepository;

    public SMSCodeDeletionService(SMSCodeRepository SMSCodeRepository) {
        this.SMSCodeRepository = SMSCodeRepository;
    }

    @Async
    @Transactional
    public void deleteTempCodeAfterDelay(Long id, int delayMillis) {
        try {
            Thread.sleep(delayMillis);
            SMSCodeRepository.deleteTempCodeById(id);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
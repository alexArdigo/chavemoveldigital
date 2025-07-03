package pt.gov.chavemoveldigital.entities;

import jakarta.persistence.*;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.Random;

@Entity
public class SMSCode {
    @Id
    @GeneratedValue
    private Long id;
    private Integer code;
    private String telephoneNumber;
    private String hashedPin;

    public SMSCode() {
    }

    public SMSCode(String telephoneNumber, Integer pin) {
        this.telephoneNumber = telephoneNumber;
        this.code = generateCode();
        this.hashedPin = hashPin(pin);
    }

    public Integer generateCode() {
        Random random = new Random();
        int min = 100000;
        int max = 999999;
        return random.nextInt(max - min) + min;
    }

    private String hashPin(Integer pin) {
        return BCrypt.hashpw(pin.toString(), BCrypt.gensalt());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public String getHashedPin() {
        return hashedPin;
    }

    public void setHashedPin(String hashedPin) {
        this.hashedPin = hashedPin;
    }
}

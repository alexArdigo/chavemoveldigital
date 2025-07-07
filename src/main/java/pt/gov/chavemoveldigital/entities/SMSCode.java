package pt.gov.chavemoveldigital.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Random;

@Entity
public class SMSCode {
    @Id
    @GeneratedValue
    private Long id;
    private Integer code;
    private String token;
    private LocalDateTime timestamp;
    private String telephoneNumber;

    public SMSCode() {
    }

    public SMSCode(String telephoneNumber, String token) {
        this.code = generateCode();
        this.token = token;
        this.timestamp = LocalDateTime.now();
        this.telephoneNumber = telephoneNumber;
    }

    public Integer generateCode() {
        Random random = new Random();
        int min = 100000;
        int max = 999999;
        return random.nextInt(max - min) + min;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

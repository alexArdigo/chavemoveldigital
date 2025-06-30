package pt.gov.chavemoveldigital.entities;

import jakarta.persistence.*;

@Entity
public class SMSCode {
    @Id
    @GeneratedValue
    Long id;
    Integer code;
    String telephoneNumber;

    public SMSCode() {
    }

    public SMSCode(String telephoneNumber, Integer code) {
        this.telephoneNumber = telephoneNumber;
        this.code = code;
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
}

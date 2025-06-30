package pt.gov.chavemoveldigital.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class AuthCode {

    @Id
    @GeneratedValue
    private Long id;

    private String code;
    private String clientId;

    public AuthCode() {
    }

    public AuthCode(String code, String clientId) {
        this.code = code;
        this.clientId = clientId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String client_id) {
        this.clientId = client_id;
    }
}

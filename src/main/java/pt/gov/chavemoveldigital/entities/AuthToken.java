package pt.gov.chavemoveldigital.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class AuthToken {

    @Id
    @GeneratedValue
    private Long id;

    private String token;
    private String clientId;

    public AuthToken() {
    }

    public AuthToken(String token, String clientId) {
        this.token = token;
        this.clientId = clientId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}

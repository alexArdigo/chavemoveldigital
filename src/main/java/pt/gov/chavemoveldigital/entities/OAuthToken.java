package pt.gov.chavemoveldigital.entities;

import jakarta.persistence.*;

@Entity
public class OAuthToken {

    @Id
    @GeneratedValue
    private Long id;

    private String token;

    @ManyToOne
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    private Client clientId;
    private String redirectUri;

    public OAuthToken() {
    }

    public OAuthToken(String token, Client clientId, String redirectUri) {
        this.token = token;
        this.clientId = clientId;
        this.redirectUri = redirectUri;
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

    public Client getClientId() {
        return clientId;
    }

    public void setClientId(Client clientId) {
        this.clientId = clientId;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectId) {
        this.redirectUri = redirectId;
    }
}

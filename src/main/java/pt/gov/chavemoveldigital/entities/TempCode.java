package pt.gov.chavemoveldigital.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

import java.util.Random;

@Entity
public class TempCode {
    @Id
    @GeneratedValue
    Long id;
    Integer code;
    @OneToOne
    User user;

    public TempCode() {
    }

    public TempCode(User user) {
        this.user = user;
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
        return generateCode();
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

package pt.gov.chavemoveldigital.entities;

import jakarta.persistence.*;
import org.aspectj.apache.bcel.classfile.Code;

import java.util.Random;

@Entity
public class TempCode {
    @Id
    @GeneratedValue
    Long id;
    Integer code;
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    User user;

    public TempCode() {
    }

    public TempCode(User user, Integer code) {
        this.user = user;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

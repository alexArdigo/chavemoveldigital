package pt.gov.chavemoveldigital.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import pt.gov.chavemoveldigital.enums.ElectoralCircle;
import pt.gov.chavemoveldigital.enums.Parish;
import pt.gov.chavemoveldigital.models.UsersDTO;

@Entity
public class User {

        @Id
        Long nif;
        String telephoneNumber;
        Integer pin;
        String firstName;
        String lastName;
        ElectoralCircle ElectoralCircle;
        Parish parish;

    public User() {
    }

    public User(UsersDTO usersDTO) {
        this.telephoneNumber = usersDTO.getTelephoneNumber();
        this.pin = usersDTO.getPin();
        this.firstName = usersDTO.getFirstName();
        this.lastName = usersDTO.getLastName();
        ElectoralCircle = usersDTO.getElectoralCircle();
        this.parish = usersDTO.getParish();
    }

    public Long getNif() {
        return nif;
    }

    public void setNif(Long nif) {
        this.nif = nif;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public Integer getPin() {
        return pin;
    }

    public void setPin(Integer pin) {
        this.pin = pin;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public ElectoralCircle getElectoralCircle() {
        return ElectoralCircle;
    }

    public void setElectoralCircle(ElectoralCircle electoralCircle) {
        ElectoralCircle = electoralCircle;
    }

    public Parish getParish() {
        return parish;
    }

    public void setParish(Parish parish) {
        this.parish = parish;
    }
}

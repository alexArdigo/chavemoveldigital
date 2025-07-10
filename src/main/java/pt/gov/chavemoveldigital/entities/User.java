package pt.gov.chavemoveldigital.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import pt.gov.chavemoveldigital.models.UsersDTO;

@Entity
public class User {

    @Id
    @GeneratedValue
    Long id;
    Long nif;
    String telephoneNumber;
    @JsonIgnore
    String pin;
    String firstName;
    String lastName;
    String district;
    String municipality;
    String parish;

    public User() {
    }

    public User(UsersDTO usersDTO, String pin) {
        this.telephoneNumber = usersDTO.getTelephoneNumber();
        this.pin = pin;
        this.nif = usersDTO.getNif();
        this.firstName = usersDTO.getFirstName();
        this.lastName = usersDTO.getLastName();
        this.district = usersDTO.getDistrict();
        this.municipality = usersDTO.getMunicipality();
        this.parish = usersDTO.getParish();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
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

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    public String getParish() {
        return parish;
    }

    public void setParish(String parish) {
        this.parish = parish;
    }
}

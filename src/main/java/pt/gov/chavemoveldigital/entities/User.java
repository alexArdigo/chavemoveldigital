package pt.gov.chavemoveldigital.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import pt.gov.chavemoveldigital.enums.ElectoralCircle;
import pt.gov.chavemoveldigital.enums.Municipality;
import pt.gov.chavemoveldigital.models.UsersDTO;

@Entity
public class User {

    @Id
    @GeneratedValue
    Long id;
    @JsonIgnore
    Long nif;
    String telephoneNumber;
    Integer pin;
    String firstName;
    String lastName;
    ElectoralCircle ElectoralCircle;
    Municipality municipality;

    public User() {
    }

    public User(UsersDTO usersDTO) {
        this.telephoneNumber = usersDTO.getTelephoneNumber();
        this.pin = usersDTO.getPin();
        this.nif = usersDTO.getNif();
        this.firstName = usersDTO.getFirstName();
        this.lastName = usersDTO.getLastName();
        ElectoralCircle = usersDTO.getElectoralCircle();
        this.municipality = usersDTO.getMunicipality();
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

    public Municipality getMunicipality() {
        return municipality;
    }

    public void setMunicipality(Municipality municipality) {
        this.municipality = municipality;
    }
}

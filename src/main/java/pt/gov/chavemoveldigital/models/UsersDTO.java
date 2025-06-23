package pt.gov.chavemoveldigital.models;

import pt.gov.chavemoveldigital.enums.ElectoralCircle;
import pt.gov.chavemoveldigital.enums.Municipality;

public class UsersDTO {

    String telephoneNumber;
    Integer pin;
    Long nif;
    String firstName;
    String lastName;
    ElectoralCircle ElectoralCircle;
    Municipality municipality;

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

    public Long getNif() {
        return nif;
    }

    public void setNif(Long nif) {
        this.nif = nif;
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

    public Municipality getParish() {
        return municipality;
    }

    public void setParish(Municipality municipality) {
        this.municipality = municipality;
    }
}

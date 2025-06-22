package pt.gov.chavemoveldigital.models;

import pt.gov.chavemoveldigital.enums.ElectoralCircle;
import pt.gov.chavemoveldigital.enums.Parish;

public class UsersDTO {

    String telephoneNumber;
    Integer pin;
    String firstName;
    String lastName;
    ElectoralCircle ElectoralCircle;
    Parish parish;

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

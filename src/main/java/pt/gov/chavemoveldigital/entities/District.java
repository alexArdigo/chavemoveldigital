package pt.gov.chavemoveldigital.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class District {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @OneToMany
    private List<Municipality> municipalities;


    public District() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Municipality> getMunicipalities() {
        return municipalities;
    }

    public void setMunicipalities(List<Municipality> municipality) {
        this.municipalities = municipality;
    }
}

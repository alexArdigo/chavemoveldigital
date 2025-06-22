package pt.gov.chavemoveldigital.services;

import jakarta.annotation.PostConstruct;

public interface UserService  {

    @PostConstruct
    void init();
}

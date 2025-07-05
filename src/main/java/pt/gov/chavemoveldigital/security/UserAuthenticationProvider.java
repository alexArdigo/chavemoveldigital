package pt.gov.chavemoveldigital.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import pt.gov.chavemoveldigital.entities.User;
import pt.gov.chavemoveldigital.repositories.UserRepository;
import pt.gov.chavemoveldigital.services.UserAuthService;

import java.util.ArrayList;
import java.util.List;


@Component
public class UserAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserAuthService userAuthService;
    @Autowired
    private UserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String telephone = authentication.getName();
        String pin = authentication.getCredentials().toString();

        User user = userRepository.findUserByTelephoneNumber(telephone);

        if (user != null) {
            List<GrantedAuthority> roles = new ArrayList<>();
            roles.add(new SimpleGrantedAuthority("ROLE_USER"));
            return new UsernamePasswordAuthenticationToken(telephone, pin, roles);
        }

        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}

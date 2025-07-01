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
import pt.gov.chavemoveldigital.services.UserAuthService;

import java.util.ArrayList;
import java.util.List;


@Component
public class UserAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserAuthService userAuthService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String telephone = authentication.getName();
        Integer pin = Integer.valueOf(authentication.getCredentials().toString());

        User user = userAuthService.validateUser(telephone, pin);
        if (user != null) {
            List<GrantedAuthority> roles = new ArrayList<>();
            roles.add(new SimpleGrantedAuthority("ROLE_USER"));
            /*if(user.getRole() == Role.ADMIN) {
                roles.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }*/
            return new UsernamePasswordAuthenticationToken(telephone, pin, roles);
        }

        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}

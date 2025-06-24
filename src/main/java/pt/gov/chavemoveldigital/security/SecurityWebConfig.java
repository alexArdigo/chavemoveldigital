package pt.gov.chavemoveldigital.security;

import jakarta.servlet.DispatcherType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityWebConfig {

    @Autowired
    UserAuthenticationProvider userAuthenticationProvider;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:8081"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors(corsConfigurer -> {
            corsConfigurer.configurationSource(corsConfigurationSource());
        });

        httpSecurity.csrf(csrfConfigurer -> {
            csrfConfigurer.disable();
        });

        httpSecurity.authorizeHttpRequests(auth -> {
            auth.dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll();
           // auth.requestMatchers("/login", "/logged", "/users").permitAll();

/*            auth.requestMatchers(
                    HttpMethod.POST, ... ).hasRole("ADMIN");*/


/*            auth.requestMatchers(
                    HttpMethod.POST,  ... ).authenticated();*/


/*            auth.requestMatchers( ... ).authenticated();*/

            auth.requestMatchers("/authentication", "**").permitAll();

            //auth.requestMatchers("**").denyAll();
        });
       /* httpSecurity.formLogin(loginConfig -> {
            loginConfig.loginPage("/login");
            loginConfig.loginProcessingUrl("/login");
            loginConfig.successHandler((request, response, authentication) -> {
                response.setStatus(200);
            });
            loginConfig.failureHandler((request, response, authentication) -> {
                response.setStatus(401);
            });
        });*/

        /*httpSecurity.logout(
                logout -> {
                    logout.logoutUrl("/logout");
                    logout.deleteCookies("JSESSIONID");
                    logout.logoutSuccessHandler((request, response, authentication) -> {
                        response.setStatus(HttpServletResponse.SC_OK);
                    });
                });*/

        httpSecurity.authenticationProvider(userAuthenticationProvider);

        return httpSecurity.build();
    }
}

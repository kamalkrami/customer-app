package net.kamal.customerfrontthymeleafapp.securite;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.SecurityFilterChain;

import java.util.*;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    // All the Permitted Url
    private static final String[] AUTH_WHITELIST = {
            "/",
            "/webjars/**",
            "/h2-console/**",
            "/loginpage/**"
    };

    private ClientRegistrationRepository clientRegistrationRepository;

    public SecurityConfig(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            return http
                 .csrf(Customizer.withDefaults())
                 .authorizeHttpRequests(auth -> {
                     auth.requestMatchers(AUTH_WHITELIST).permitAll();
                     auth.anyRequest().authenticated();
                 })
                 .oauth2Login(al->
                         al.loginPage("/loginpage")
                         .defaultSuccessUrl("/"))
                 .logout((logout)-> logout
                    .logoutSuccessHandler(oidcLogoutSuccessHandler())
                    .logoutSuccessUrl("/").permitAll()
                    .clearAuthentication(true)
                    .deleteCookies("JSESSIONID"))
                 .exceptionHandling(eh->eh.accessDeniedPage("/notAuthorized"))
                 .build();

    }
    // Function For Logout Keycloak
    private OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler(){
        final OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler =
                new OidcClientInitiatedLogoutSuccessHandler(this.clientRegistrationRepository);
        oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}?logoutsuccess=true");
        return oidcLogoutSuccessHandler;
    }
    // Function For To Test What type of Authentication We Are Using OIDC_AUTH or OAUTH2_AUTH
    @Bean
    public GrantedAuthoritiesMapper userAuthoritiesMapper(){
        return (authorities -> {
            final Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
            authorities.forEach((authority)->{
                if(authority instanceof OidcUserAuthority oidcAuth){
                    // Calling mapAuthrities
                    mappedAuthorities.addAll(mapAuthrities(oidcAuth.getIdToken().getClaims()));
                    //System.out.println(oidcAuth.getAttributes());
                } else if (authority instanceof OAuth2UserAuthority oauth2Auth) {
                    // Calling mapAuthrities
                    mappedAuthorities.addAll(mapAuthrities(oauth2Auth.getAttributes()));
                }
            });
            return mappedAuthorities;
        });
    }
    // Function mapAuthrities is Add to the Connection Object the roles of the User Connected As a Simple Granted Authority
    private List<SimpleGrantedAuthority> mapAuthrities(final Map<String,Object> attributes){
        final Map<String, Object> realmAccess = ((Map<String, Object>)attributes.getOrDefault("realm_access",Collections.emptyMap()) );
        final Collection<String> roles = ((Collection<String>)realmAccess.getOrDefault("roles",Collections.emptyList()));
        return roles.stream()
                .map((role) -> new SimpleGrantedAuthority(role))
                .toList();
    }
}

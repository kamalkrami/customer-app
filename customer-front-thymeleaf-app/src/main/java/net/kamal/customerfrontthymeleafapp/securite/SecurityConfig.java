package net.kamal.customerfrontthymeleafapp.securite;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final String[] AUTH_WHITELIST = {
            "/",
            "/webjars/**",
            "/h2-console/**",
    };
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            return http
                 .csrf(Customizer.withDefaults())
                 .authorizeHttpRequests(auth -> {
                     auth.requestMatchers(AUTH_WHITELIST).permitAll();
                     auth.anyRequest().authenticated();
                 })
                 .oauth2Login(Customizer.withDefaults())
                 .logout((logout)-> logout
                    //.logoutSuccessHandler(oidcLogoutSuccessHandler())
                    .logoutSuccessUrl("/").permitAll()
                    .deleteCookies("JSESSIONID")
                 ).build();

    }
}

package org.danielaguilar.samples.jeeauth;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests(requests -> requests
            .antMatchers("/admin/**").hasRole("ADMIN")
            .anyRequest().permitAll()
        );
        http.jee(jee -> jee.mappableAuthorities("ROLE_ADMIN"));
    }
}

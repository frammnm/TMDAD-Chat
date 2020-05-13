package com.example.demochat.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import com.example.demochat.service.*;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.sql.DataSource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    DataSource dataSource;

    @Autowired
    private MyUserDetailsService userDetailsService;



    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests(
                    authorize -> authorize
                                .anyRequest()
                                .authenticated()
            )
            .formLogin()
            .successHandler(successHandler()).and()
            .httpBasic().and()
            .logout()
            .deleteCookies("JSESSIONID")
            .invalidateHttpSession(true);

    }

    private AuthenticationSuccessHandler successHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException {
//                httpServletResponse.getWriter().append("OK");
//                httpServletResponse.setStatus(200);
                System.out.println("Entre");
                System.out.println(httpServletRequest);
                System.out.println(httpServletResponse);
                System.out.println(authentication.getPrincipal());
                httpServletResponse.sendRedirect("/");
            }
        };
    }

    @Autowired
    public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider());
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

}
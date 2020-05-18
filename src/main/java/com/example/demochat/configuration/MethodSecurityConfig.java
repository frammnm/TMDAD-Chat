package com.example.demochat.configuration;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.method.configuration.*;

@Configuration
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

}
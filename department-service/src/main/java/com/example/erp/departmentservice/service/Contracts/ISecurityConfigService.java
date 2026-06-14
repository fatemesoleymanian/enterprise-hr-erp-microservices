package com.example.erp.departmentservice.service.Contracts;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

public interface ISecurityConfigService
{
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception ;
    JwtAuthenticationConverter jwtAuthenticationConverter();
}

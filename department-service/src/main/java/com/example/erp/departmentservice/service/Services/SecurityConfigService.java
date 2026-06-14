package com.example.erp.departmentservice.service.Services;

import com.example.erp.departmentservice.service.Contracts.ISecurityConfigService;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;
import java.util.stream.Collectors;

public class SecurityConfigService implements ISecurityConfigService
{
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/departments/**").hasAnyRole("ADMIN", "HR_MANAGER")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {

            Object rolesObj = jwt.getClaim("roles");

            if (rolesObj == null) {
                return List.of();
            }

            List<String> roles;

            if (rolesObj instanceof List<?> list) {
                roles = list.stream()
                        .map(String::valueOf)
                        .toList();
            } else {
                roles = List.of(rolesObj.toString());
            }

            return roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.<GrantedAuthority>toList());
        });

        return converter;
    }
}

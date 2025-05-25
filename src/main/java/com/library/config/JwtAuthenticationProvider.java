package com.library.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;


    @Override
    public Authentication authenticate(Authentication authentication) {
        String token = (String) authentication.getCredentials();
        String username = jwtService.extractName(token);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if(jwtService.isTokenValid(token, userDetails)) {
            return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());

        }
        return null;

    }
    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

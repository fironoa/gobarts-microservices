package com.gobartsdev.service;

import com.gobartsdev.exception.JwtAuthenticationException;
import com.gobartsdev.model.request.AuthenticationRequest;
import com.gobartsdev.model.response.AuthenticationResponse;
import com.gobartsdev.util.JwtUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final Logger logger = LogManager.getLogger(AuthService.class);

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthService(AuthenticationManager authenticationManager,
                       JwtUtil jwtUtil){
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public AuthenticationResponse login(AuthenticationRequest request) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        if(!auth.isAuthenticated()){
            throw new UsernameNotFoundException("Authentication failed");
        }

        Set<String> roles = new HashSet<>();
        for(GrantedAuthority ga : auth.getAuthorities()){
            System.out.println(ga);
            roles.add(ga.getAuthority());
        }

        String accessToken = jwtUtil.generateAccessToken(request.getUsername(), roles);
        String refreshToken = jwtUtil.generateRefreshToken(request.getUsername(), roles);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse refreshToken(String refreshToken) {

        try{
            if(jwtUtil.isAValidToken(refreshToken)){
                String accessToken = jwtUtil.generateRefreshToken(
                        jwtUtil.extractUsername(refreshToken),
                        jwtUtil.getRolesFromToken(refreshToken).stream().map(Object::toString).collect(Collectors.toSet())
                );
                return AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
            }
            logger.info("Refresh token is invalid. refresh failed.");
            throw new JwtAuthenticationException("Refresh Token is not valid");

        } catch (Exception ex){
            logger.info("Refresh token failed while parsing. It is either invalid or expired");
            throw new JwtAuthenticationException("Refresh token failed while parsing. It is either invalid or expired");
        }
    }


    public Map<String, Boolean> verifyToken(String token) {
        try{
            return Map.of(
                        "verified", jwtUtil.isAValidToken(token)
            );
        } catch (Exception ex){
            logger.info("Refresh token failed while parsing. It is either invalid or expired");
            throw new JwtAuthenticationException("Refresh token failed while parsing. It is either invalid or expired");
        }

    }
}

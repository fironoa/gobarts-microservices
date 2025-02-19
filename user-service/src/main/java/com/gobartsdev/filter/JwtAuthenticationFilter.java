package com.gobartsdev.filter;

import com.gobartsdev.constants.ApiDictionary;
import com.gobartsdev.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final Logger logger = LogManager.getLogger(JwtAuthenticationFilter.class);
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil,
                                   UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request,
                                 HttpServletResponse response,
                                 FilterChain filterChain)
            throws IOException, ServletException {

        logger.info(request.getRequestURI() + " is being accessed");
        logger.info("Before auth : " + System.currentTimeMillis());
        if (ApiDictionary.getOpenApis().contains(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = getJwtFromRequest(request);

        if(token == null){
            logger.info("Token not found in the header");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(
                    "Token not found"
            );
            return;
        }

        try{
            // Checking if it is valid because it can't parse an invalid token.
            // and it will be caught in the catch block.
            logger.info("Before validating token : " + System.currentTimeMillis());
            if(jwtUtil.isAValidToken(token)){
                logger.info("After validating token : " + System.currentTimeMillis());
                String username = jwtUtil.extractUsername(token);

                Collection<GrantedAuthority> roles = jwtUtil.getRolesFromToken(token).stream()
                        .map(Object::toString)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                if(SecurityContextHolder.getContext().getAuthentication() == null) {
                    //UserDetails user = userDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            roles
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
                logger.info("After auth is set : " + System.currentTimeMillis());
                filterChain.doFilter(request, response);
            }
        } catch(Exception ex){
            logger.info("Token is either expired or invalid");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(
                    "{\n" +
                            "    \"message\": \"Token is expired/invalid\",\n" +
                            "    \"timestamp\": \""+ Instant.now() +"\"\n" +
                            "}"
            );
        }

    }


    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}

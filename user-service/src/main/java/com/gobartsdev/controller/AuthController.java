package com.gobartsdev.controller;

import com.gobartsdev.service.AuthService;
import com.gobartsdev.model.request.AuthenticationRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.gobartsdev.constants.ApiDictionary.*;


@RestController
@RequestMapping(API_V1)
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping(LOGIN)
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request){
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping(REFRESH_TOKEN)
    public ResponseEntity<?> refreshToken(HttpServletRequest request){
        String refreshToken = request.getHeader("Refresh-Token");
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }

    @GetMapping(VERIFY)
    public ResponseEntity<?> verifyToken(@RequestParam("token") String token) {
        if(token == null){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(authService.verifyToken(token));
    }

}

package com.gobartsdev.controller;


import com.gobartsdev.model.dto.UserDto;
import com.gobartsdev.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import static com.gobartsdev.constants.ApiDictionary.*;


@RestController
@RequestMapping(API_V1)
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }


    @GetMapping("protected")
    public ResponseEntity<?> tryProtected(HttpServletRequest request){

        SecurityContext context = SecurityContextHolder.getContext();
        Authentication auth = context.getAuthentication();

        for(GrantedAuthority d : auth.getAuthorities()){
            System.out.println(d);
            System.out.println("\n");
        }

        return ResponseEntity.ok("Accessible : " +
                request.getSession().getId());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(ADMIN + USERS)
    public ResponseEntity<?> getAllUsers(){
        return ResponseEntity.ok(userService.findAllUser());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(ADMIN + USERS + ROLES)
    public ResponseEntity<?> updateRoles(@RequestBody UserDto userDto){
        return ResponseEntity.ok(userService.updateRoles(userDto));
    }


    @PostMapping(REGISTER)
    public ResponseEntity<?> createUser(@RequestBody UserDto user){
        return ResponseEntity.ok(userService.createUser(user));
    }

}

package com.gobartsdev.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gobartsdev.model.entity.RoleEntity;
import com.gobartsdev.model.entity.UserEntity;
import jakarta.persistence.Column;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {
    private Long id;
    private String name;
    private String username;
    private String email;
    private String phoneNumber;
    private String password;
    private Set<String> rolesNames;

    public UserDto(UserEntity user){
        this.id = user.getId();
        this.name = user.getName();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.rolesNames = user.getRoles().stream().map(RoleEntity::getName).collect(Collectors.toSet());

    }
}

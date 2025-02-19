package com.gobartsdev.service;

import com.gobartsdev.exception.BadRequestException;
import com.gobartsdev.exception.ResourceNotFoundException;
import com.gobartsdev.model.dto.UserDto;
import com.gobartsdev.model.entity.RoleEntity;
import com.gobartsdev.model.entity.UserEntity;
import com.gobartsdev.repository.RoleRepository;
import com.gobartsdev.repository.UserRepository;
import com.gobartsdev.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Collection;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder
    ){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserEntity createUser(UserDto userDto){

        UserEntity user = new UserEntity(userDto);
        user.setActivated(false);
        user.setPassword(
                passwordEncoder.encode(userDto.getPassword())
        );

        RoleEntity role = roleRepository.findByName("USER");
        if(role != null){
            user.setRoles(
                    Set.of(role)
            );
        }

        return userRepository.save(user);

    }

    public UserEntity updateRoles(UserDto userDto){

        if(ObjectUtils.isEmpty(userDto.getId())){
            throw new BadRequestException("user", "id", String.valueOf(userDto.getId()));
        }

        UserEntity user = userRepository.findById(userDto.getId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("user", String.valueOf(userDto.getId()))
                );

        Collection<RoleEntity> rolesByName = roleRepository.findAllByNameIn(userDto.getRolesNames());
        if(!rolesByName.isEmpty()){
            user.getRoles().addAll(rolesByName);
        }
        return userRepository.save(user);
    }



    public UserEntity getUserByUsername(String username){
        UserEntity user = userRepository.findByUsername(username);
        if(ObjectUtils.isEmpty(user)){
            throw new UsernameNotFoundException("User not found");
        }
        return user;

    }


    public Collection<UserDto> findAllUser() {
        return userRepository.findAll()
                .stream().map(UserDto::new).toList();
    }
}

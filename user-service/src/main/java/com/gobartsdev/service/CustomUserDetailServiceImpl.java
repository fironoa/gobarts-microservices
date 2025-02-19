package com.gobartsdev.service;

import com.gobartsdev.model.entity.RoleEntity;
import com.gobartsdev.model.entity.UserEntity;
import com.gobartsdev.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CustomUserDetailServiceImpl implements UserDetailsService{

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       UserEntity user = userRepository.findByUsername(username);

       if(user == null){
           throw new UsernameNotFoundException("User not found with username : " + username);
       }

       String[] roles = user.getRoles().stream().map(RoleEntity::getName).distinct().toArray(String[]::new);
       return User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .roles(roles)
                    .build();


    }
}

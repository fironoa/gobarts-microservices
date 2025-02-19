package com.gobartsdev.repository;

import com.gobartsdev.model.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Set;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    RoleEntity findByName(String name);

    Collection<RoleEntity> findAllByNameIn(Set<String> rolesNames);
}

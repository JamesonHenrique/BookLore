package com.jhcs.booklore.domain.repository;

import com.jhcs.booklore.domain.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {

    Optional<Role> findByName(String role);
}

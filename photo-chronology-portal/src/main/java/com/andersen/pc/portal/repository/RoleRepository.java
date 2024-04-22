package com.andersen.pc.portal.repository;

import com.andersen.pc.common.model.UserRole;
import com.andersen.pc.common.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByRoleName(UserRole roleName);
}

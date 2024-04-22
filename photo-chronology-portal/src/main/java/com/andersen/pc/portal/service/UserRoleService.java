package com.andersen.pc.portal.service;

import com.andersen.pc.common.model.entity.Role;
import com.andersen.pc.common.model.entity.User;
import com.andersen.pc.common.model.entity.UserRole;
import com.andersen.pc.portal.repository.RoleRepository;
import com.andersen.pc.portal.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;

    public User createUserRole(User user) {
        UserRole userRole = new UserRole();
        Role role = roleRepository.findByRoleName(com.andersen.pc.common.model.UserRole.USER);
        userRole.setRole(role);
        userRole.setUser(user);
        userRole = userRoleRepository.save(userRole);
        user.setUserRoles(Set.of(userRole));
        return user;
    }
}

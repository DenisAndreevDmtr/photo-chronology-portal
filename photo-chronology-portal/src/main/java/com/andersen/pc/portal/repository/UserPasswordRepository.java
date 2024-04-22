package com.andersen.pc.portal.repository;

import com.andersen.pc.common.model.entity.User;
import com.andersen.pc.common.model.entity.UserPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPasswordRepository extends JpaRepository<UserPassword, Long> {

    Optional<UserPassword> findByUserId(Long userId);
    Optional<UserPassword> findByUser(User user);
}

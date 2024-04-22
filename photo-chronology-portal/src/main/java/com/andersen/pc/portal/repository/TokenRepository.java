package com.andersen.pc.portal.repository;

import com.andersen.pc.common.model.entity.Token;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface TokenRepository extends ListCrudRepository<Token, Long> {

    Optional<Token> findTokenByToken(String token);

    Set<Token> findTokensByUserId(String userId);
}

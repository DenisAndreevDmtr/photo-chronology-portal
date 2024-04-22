package com.andersen.pc.portal.configuration;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;

@AutoConfiguration
public class JpaConfiguration {

    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public AuditorAware<String> auditorAware() {
        return new SecurityAuditorAware();
    }

    @Bean
    public JPAQueryFactory jpaQueryFactoryBean() {
        return new JPAQueryFactory(entityManager);
    }
}

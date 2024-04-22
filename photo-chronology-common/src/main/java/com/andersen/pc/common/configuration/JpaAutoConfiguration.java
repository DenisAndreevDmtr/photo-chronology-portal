package com.andersen.pc.common.configuration;

import com.andersen.pc.common.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;

@AutoConfiguration
@EntityScan(Constant.Configuration.PACKAGES_TO_SCAN)
@EnableJpaRepositories(Constant.Configuration.PACKAGES_TO_SCAN)
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@Slf4j
public class JpaAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(AuditorAware.class)
    public AuditorAware<String> auditorAware() {
        log.info("Auditor aware not found. Default one will be initialized");
        return () -> Optional.of("PC_PORTAL");
    }
}

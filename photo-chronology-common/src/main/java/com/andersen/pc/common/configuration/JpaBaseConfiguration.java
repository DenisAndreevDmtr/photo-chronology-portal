package com.andersen.pc.common.configuration;

import com.andersen.pc.common.constant.Constant;
import lombok.RequiredArgsConstructor;
import org.hibernate.engine.transaction.jta.platform.internal.NoJtaPlatform;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.data.domain.AuditorAware;
import org.springframework.orm.hibernate5.SpringBeanContainer;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class JpaBaseConfiguration {

    private final JpaProperties properties;
    private final ConfigurableListableBeanFactory beanFactory;

    protected AuditorAware<String> auditorAware(String auditorName) {
        return () -> Optional.of(auditorName);
    }

    protected PlatformTransactionManager platformTransactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactoryBean) {
        var dataSource = entityManagerFactoryBean.getDataSource();
        var baseTransactionManager = new JpaTransactionManager();
        var baseEntityManagerFactory = entityManagerFactoryBean.getObject();
        baseTransactionManager.setEntityManagerFactory(baseEntityManagerFactory);
        baseTransactionManager.setNestedTransactionAllowed(true);
        baseTransactionManager.setDataSource(dataSource);
        baseTransactionManager.setJpaDialect(new HibernateJpaDialect());
        return baseTransactionManager;
    }

    private JpaVendorAdapter jpaVendorAdapter() {
        var adapter = new HibernateJpaVendorAdapter();
        adapter.setShowSql(properties.isShowSql());
        if (Objects.nonNull(properties.getDatabase())) {
            adapter.setDatabase(properties.getDatabase());
        }
        if (Objects.nonNull(properties.getDatabasePlatform())) {
            adapter.setDatabasePlatform(properties.getDatabasePlatform());
        }
        adapter.setGenerateDdl(properties.isGenerateDdl());
        adapter.setPrepareConnection(true);
        return adapter;
    }

    protected LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(
            DataSource dataSource,
            String persistenceUnit) {
        var entityManager = new LocalContainerEntityManagerFactoryBean();
        entityManager.setDataSource(dataSource);
        entityManager.setPackagesToScan(Constant.Configuration.PACKAGES_TO_SCAN);
        entityManager.setJpaVendorAdapter(jpaVendorAdapter());
        entityManager.setPersistenceUnitName(persistenceUnit);
        entityManager.setJpaDialect(new HibernateJpaDialect());
        entityManager.getJpaPropertyMap().putAll(composeJpaProperties());
        return entityManager;
    }

    private Map<String, Object> composeJpaProperties() {
        var jpaPropertyMap = new HashMap<String, Object>();
        jpaPropertyMap.put("hibernate.transaction.jta.platform", new NoJtaPlatform());
        jpaPropertyMap.put("hibernate.resource.beans.container", new SpringBeanContainer(beanFactory));
        jpaPropertyMap.put("hibernate.implicit_naming_strategy",
                "org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy");
        jpaPropertyMap.put("hibernate.archive.scanner",
                "org.hibernate.boot.archive.scan.internal.DisabledScanner");
        jpaPropertyMap.put("hibernate.physical_naming_strategy",
                "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");
        return jpaPropertyMap;
    }
}

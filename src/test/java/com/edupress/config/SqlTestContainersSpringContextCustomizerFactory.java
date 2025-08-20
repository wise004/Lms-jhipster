package com.edupress.config;

import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;
import org.springframework.test.context.MergedContextConfiguration;

public class SqlTestContainersSpringContextCustomizerFactory implements ContextCustomizerFactory {

    private Logger log = LoggerFactory.getLogger(SqlTestContainersSpringContextCustomizerFactory.class);

    private static SqlTestContainer prodTestContainer;

    @Override
    public ContextCustomizer createContextCustomizer(
        @NonNull Class<?> testClass,
        @NonNull List<ContextConfigurationAttributes> configAttributes
    ) {
        return new ContextCustomizer() {
            @Override
            public void customizeContext(
                @NonNull ConfigurableApplicationContext context,
                @NonNull MergedContextConfiguration mergedConfig
            ) {
                ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
                TestPropertyValues testValues = TestPropertyValues.empty();
                EmbeddedSQL sqlAnnotation = AnnotatedElementUtils.findMergedAnnotation(testClass, EmbeddedSQL.class);
                if (null != sqlAnnotation) {
                    log.debug("detected the EmbeddedSQL annotation on class {}", testClass.getName());
                    log.info("Warming up the sql database");
                    if (null == prodTestContainer) {
                        try {
                            @SuppressWarnings("unchecked")
                            Class<? extends SqlTestContainer> containerClass = (Class<? extends SqlTestContainer>) Class.forName(
                                this.getClass().getPackageName() + ".PostgreSqlTestContainer"
                            );
                            prodTestContainer = beanFactory.createBean(containerClass);
                            beanFactory.registerSingleton(containerClass.getName(), prodTestContainer);
                            /**
                             * ((DefaultListableBeanFactory)beanFactory).registerDisposableBean(containerClass.getName(), prodTestContainer);
                             */
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (prodTestContainer != null && prodTestContainer.getTestContainer() != null) {
                        testValues = testValues.and("spring.datasource.url=" + prodTestContainer.getTestContainer().getJdbcUrl() + "");
                        testValues = testValues.and("spring.datasource.username=" + prodTestContainer.getTestContainer().getUsername());
                        testValues = testValues.and("spring.datasource.password=" + prodTestContainer.getTestContainer().getPassword());
                    } else {
                        log.warn("Testcontainers is not available. Falling back to H2 in-memory datasource for tests.");
                        String dbName = (testClass.getSimpleName() + "_" + UUID.randomUUID()).replace('-', '_');
                        testValues = testValues.and("spring.datasource.type=com.zaxxer.hikari.HikariDataSource");
                        testValues = testValues.and("spring.datasource.driver-class-name=org.h2.Driver");
                        testValues = testValues.and(
                            "spring.datasource.url=jdbc:h2:mem:" +
                            dbName +
                            ";DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH"
                        );
                        testValues = testValues.and("spring.datasource.username=sa");
                        testValues = testValues.and("spring.datasource.password=");
                        testValues = testValues.and("spring.jpa.database-platform=org.hibernate.dialect.H2Dialect");
                        testValues = testValues.and("spring.jpa.hibernate.ddl-auto=none");
                        // Don't use liquibase drop-first with H2 fallback; each test uses a fresh in-memory DB
                        // which avoids changelog/table collisions and bypasses Liquibase dropAll NPEs.
                    }
                }
                testValues.applyTo(context);
            }

            @Override
            public int hashCode() {
                return SqlTestContainer.class.getName().hashCode();
            }

            @Override
            public boolean equals(Object obj) {
                return this.hashCode() == obj.hashCode();
            }
        };
    }
}

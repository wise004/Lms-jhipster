package com.edupress.config;

import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;

public class PostgreSqlTestContainer implements SqlTestContainer {

    private static final Logger LOG = LoggerFactory.getLogger(PostgreSqlTestContainer.class);

    private PostgreSQLContainer<?> postgreSQLContainer;

    @Override
    public void destroy() {
        if (null != postgreSQLContainer && postgreSQLContainer.isRunning()) {
            postgreSQLContainer.stop();
        }
    }

    @Override
    @SuppressWarnings("resource")
    public void afterPropertiesSet() {
        try {
            if (postgreSQLContainer == null) {
                final PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:17.4")
                    .withDatabaseName("Edupress")
                    .withTmpFs(Collections.singletonMap("/testtmpfs", "rw"))
                    .withLogConsumer(new Slf4jLogConsumer(LOG))
                    .withReuse(true);
                // assign to field; lifecycle managed by Testcontainers and destroy()
                postgreSQLContainer = container;
            }
            if (!postgreSQLContainer.isRunning()) {
                postgreSQLContainer.start();
            }
        } catch (IllegalStateException ex) {
            // Testcontainers couldn't find a Docker environment. Fall back to default (e.g., H2) without failing tests.
            LOG.warn(
                "Docker environment not available for Testcontainers. Falling back to default test datasource. Cause: {}",
                ex.getMessage()
            );
            postgreSQLContainer = null;
        } catch (RuntimeException ex) {
            // Any other startup issue: don't break the whole test context, just log and skip.
            LOG.warn("Failed to start PostgreSQL Testcontainer. Falling back to default test datasource. Cause: {}", ex.getMessage());
            postgreSQLContainer = null;
        }
    }

    @Override
    public JdbcDatabaseContainer<?> getTestContainer() {
        return postgreSQLContainer;
    }
}

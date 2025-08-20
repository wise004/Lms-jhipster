package com.edupress.config;

// No-op stub to eliminate Elasticsearch Testcontainers dependency in tests.
public class ElasticsearchTestContainer
    implements org.springframework.beans.factory.InitializingBean, org.springframework.beans.factory.DisposableBean {

    @Override
    public void afterPropertiesSet() {}

    @Override
    public void destroy() {}

    // Minimal API used by TestContainersSpringContextCustomizerFactory
    public Object getElasticsearchContainer() {
        return new Object() {
            public String getHttpHostAddress() {
                return "localhost:0";
            }
        };
    }
}

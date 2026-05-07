package com.npci.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostgresDriverCondition implements org.springframework.context.annotation.Condition {

    private static final Logger logger = LoggerFactory.getLogger(PostgresDriverCondition.class);

    @Override
    public boolean matches(org.springframework.context.annotation.ConditionContext context,
            org.springframework.core.type.AnnotatedTypeMetadata metadata) {
        try {
            Class.forName("org.postgresql.Driver");
            logger.info("PostgreSQL Driver found. DataSource bean will be created.");
            return true;
        } catch (ClassNotFoundException e) {
            logger.warn("PostgreSQL Driver not found. DataSource bean will NOT be created.");
            return false;
        }
    }

}

package com.npci.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration("mtsConfig")
@PropertySource("classpath:application.properties")
@ComponentScan(basePackages = "com.npci")
public class MtsConfiguration {

    @Value("${spring.datasource.url}")
    private String dbUrl;
    @Value("${spring.datasource.username}")
    private String dbUsername;
    @Value("${spring.datasource.password}")
    private String dbPassword;
    @Value("${spring.datasource.hikari.maximum-pool-size:10}")
    private int maxPoolSize;
    @Value("${spring.datasource.hikari.minimum-idle:2}")
    private int minIdle;
    @Value("${spring.datasource.hikari.idle-timeout:30000}")
    private long idleTimeout;
    @Value("${spring.datasource.hikari.connection-timeout:20000}")
    private long connectionTimeout;

    public String getDbUrl() {
        return dbUrl;
    }

    @Bean
    public HikariConfig hikariConfig() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.postgresql.Driver");
        config.setJdbcUrl(dbUrl);
        config.setUsername(dbUsername);
        config.setPassword(dbPassword);
        config.setMaximumPoolSize(maxPoolSize);
        config.setMinimumIdle(minIdle);
        config.setIdleTimeout(idleTimeout);
        config.setConnectionTimeout(connectionTimeout);
        return config;
    }

    @Bean
    @Conditional(PostgresDriverCondition.class)
    public DataSource dataSourceChn(HikariConfig hikariConfig) {
        return new HikariDataSource(hikariConfig);
    }

    // @Bean
    // public AccountRepository jdbcAccountRepository(DataSource dataSource) {
    // return new JdbcAccountRepository(dataSource);
    // }

    // @Bean
    // public AccountRepository jpaAccountRepository() {
    // return new JpaAccountRepository();
    // }

    // @Bean
    // public NotificationService emailNotificationService() {
    // return new EmailNotificationService();
    // }

    // @Bean
    // public TransferService transferService(@Qualifier("jdbcAccountRepository")
    // AccountRepository accountRepository,
    // NotificationService notificationService) {
    // return new TransferServiceImpl(accountRepository, notificationService);
    // }

}

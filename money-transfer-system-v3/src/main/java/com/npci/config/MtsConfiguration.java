package com.npci.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@ComponentScan(basePackages = "com.npci")
public class MtsConfiguration {

    @Bean
    public HikariConfig hikariConfig() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.postgresql.Driver");
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/mydatabase");
        config.setUsername("postgres");
        config.setPassword("mysecretpassword");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(30000);
        config.setConnectionTimeout(20000);
        return config;
    }

    @Bean
    public DataSource dataSource(HikariConfig hikariConfig) {
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

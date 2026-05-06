package com.npci.repository;

public class AccountRepositoryFactory {

    public static AccountRepository getAccountRepository(String type) {
        if (type.equals("jdbc")) {
            return new JdbcAccountRepository();
        } else if (type.equals("jpa")) {
            return new JpaAccountRepository();
        } else {
            throw new IllegalArgumentException(type);
        }
    }

}

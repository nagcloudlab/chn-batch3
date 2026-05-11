package com.npci.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("fooService")
public class FooService {

    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRED)
    public void doWriteWithDatabase() {

        // if any failure occurs here,
        // the transaction will be rolled back, ensuring that no partial updates occur

    }

}

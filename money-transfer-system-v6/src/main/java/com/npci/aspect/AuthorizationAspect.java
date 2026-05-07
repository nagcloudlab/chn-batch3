package com.npci.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Order(1) // Ensure this aspect runs before the AtomicTransactionAspect
public class AuthorizationAspect {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AuthorizationAspect.class);

    @Before("execution(void com.npci.service.TransferService.*(..))")
    public void checkAuthorization() {
        logger.info(">>> checking authorization for transfer. 👮‍♀️👮‍♀️👮‍♀️👮‍♀️");
    }

}

package com.npci.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Order(2) // Ensure this aspect runs after the AuthorizationAspect
public class AtomicTransactionAspect {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AtomicTransactionAspect.class);

    // @Before("execution(void transfer(..))")
    // oid beforeTra fer {
    // logger.info(">>>> begin transaction.");
    // }

    // @AfterReturning("execution(void transfer(..))")
    // oid afterTran er( {
    // logger.info(">>>> commit transaction.");
    // }

    // @AfterThrowing("execution(void transfer(..))")
    // oid afterThro ng( {
    // logger.info(">>>> rollback transaction.");
    // }

    // @After("execution(void transfer(..))")
    //

    @Around("execution(void transfer(..))")
    public void aroundTransfer(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            logger.info(">>> begin transaction.");
            joinPoint.proceed(); // Proceed with the original transfer method
            logger.info(">>> commit transaction.");
        } catch (Throwable ex) {
            logger.error(">>> rollback transaction due to exception: {}", ex.getMessage());
            throw ex; // Rethrow the exception to propagate it up the call stack
        } finally {
            logger.info(">>> end transaction.");
        }
    }
}

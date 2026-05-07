package com.npci.config;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

// @Component
public class BPP implements BeanPostProcessor {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(BPP.class);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        logger.info("Before Initialization : {}", beanName);
        return bean; // you can return any other object as well
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        logger.info("After Initialization : {}", beanName);
        return bean; // you can return any other object as well
    }

}

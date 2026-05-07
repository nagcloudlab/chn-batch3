package com.npci.config;

import org.slf4j.Logger;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.stereotype.Component;

// @Component
public class BFPP implements BeanFactoryPostProcessor {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(BFPP.class);

    @Override
    public void postProcessBeanFactory(
            org.springframework.beans.factory.config.ConfigurableListableBeanFactory beanFactory)
            throws org.springframework.beans.BeansException {
        logger.info("BeanFactoryPostProcessor is called. Bean count: {}", beanFactory.getBeanDefinitionCount());
        // log all bean names String[] beanNames = beanFactory.getBeanDefinitionNames();
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            logger.info("Bean name: {}", beanName);
        }
    }

}

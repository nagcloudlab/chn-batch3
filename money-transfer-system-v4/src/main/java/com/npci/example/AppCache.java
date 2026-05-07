package com.npci.example;

import org.slf4j.Logger;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy
public class AppCache {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(AppCache.class);

    public AppCache() {
        logger.info("AppCache instance created");
    }

}

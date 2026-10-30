package org.example.collectorseervice.runner;

import org.example.collectorseervice.service.CollectorService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "collector.run-once", havingValue = "true")
public class RunOnceCollector implements ApplicationRunner {
    private final CollectorService collectorService;
    private final ApplicationContext applicationContext;

    public RunOnceCollector(CollectorService collectorService, ApplicationContext applicationContext) {
        this.collectorService = collectorService;
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(ApplicationArguments args) {
        collectorService.collectData();
        System.exit(SpringApplication.exit(applicationContext));
    }
}

package org.example.collectorseervice.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

// In run-once mode (GitHub Actions) the scheduler stays off, otherwise it would start a second collection
@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "collector.run-once", havingValue = "false", matchIfMissing = true)
public class SchedulingConfig {
}

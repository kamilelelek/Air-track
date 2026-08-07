package org.example.collectorseervice.client;

import lombok.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class OpenAqConfig {
    //@value ("${  }")
    private String baseUrl;
    //@Value ("${}")
    private String apiKey;

    @Bean
    public RestClient openAqWebClient() {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("", apiKey)
                .build();
    }
}

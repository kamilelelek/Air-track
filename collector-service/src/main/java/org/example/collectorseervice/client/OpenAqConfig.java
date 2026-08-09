package org.example.collectorseervice.client;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class OpenAqConfig {
    @Value("${openaq.base-url}")
    private String baseUrl;

    @Value("${openaq.apikey}")
    private String apiKey;

    @Bean
    public RestClient openAqWebClient() {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("X-API-Key", apiKey)
                .build();
    }
}

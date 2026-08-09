package org.example.collectorseervice.client;

import org.example.collectorseervice.dto.OpenAqDto;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Component
public class OpenAqClient {
    OpenAqConfig openAqConfig;

    OpenAqClient(OpenAqConfig openAqConfig) {
        this.openAqConfig = openAqConfig;
    }

    public List<OpenAqDto> getResponse() {
        OpenAqResponse response;
        try {
            response = openAqConfig.openAqWebClient().get()
                    .uri("/countries/42")
                    .retrieve()
                    .body(OpenAqResponse.class);
        } catch (RestClientException e) {
            throw new IllegalArgumentException("Nie udało się pobrać danych z Open AQ API", e);
        }

        if (response == null || response.results() == null) {
            throw new IllegalArgumentException("Open Aq API zwróciło pustą odpowiedź");
        }
        return response.results();
    }
}

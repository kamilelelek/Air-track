package org.example.collectorseervice.client;

import lombok.extern.slf4j.Slf4j;
import org.example.collectorseervice.dto.latest.LatestReadingDto;
import org.example.collectorseervice.dto.locations.LocationDto;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.function.Supplier;

@Slf4j
@Component
public class OpenAqClient {
    private static final int MAX_RETRIES = 3;
    private static final long DEFAULT_RETRY_WAIT_SECONDS = 5;

    OpenAqConfig openAqConfig;

    OpenAqClient(OpenAqConfig openAqConfig) {
        this.openAqConfig = openAqConfig;
    }

    public List<LocationDto> getLocationResponse() {
        LocationsResponse response = executeWithRetry(() ->
                openAqConfig.openAqWebClient().get()
                        .uri("/locations?iso=PL")
                        .retrieve()
                        .body(LocationsResponse.class));

        if (response == null || response.results() == null) {
            throw new ExternalApiException("Open Aq API zwróciło pustą odpowiedź");
        }
        return response.results();
    }

    public List<LatestReadingDto> getLatestResponse(LocationDto location) {
        LatestResponse response = executeWithRetry(() ->
                openAqConfig.openAqWebClient().get()
                        .uri("/locations/" + location.id() + "/latest")
                        .retrieve()
                        .body(LatestResponse.class));

        if (response == null || response.results() == null) {
            throw new ExternalApiException("Open Aq API zwróciło pustą odpowiedź");
        }
        return response.results();
    }

    /**
     * Wykonuje request; przy HTTP 429 czeka tyle, ile mówi nagłówek X-Ratelimit-Reset
     * (albo domyślne 5s, jeśli go brak) i próbuje ponownie, maksymalnie MAX_RETRIES razy.
     */
    private <T> T executeWithRetry(Supplier<T> request) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                return request.get();
            } catch (HttpClientErrorException.TooManyRequests e) {
                if (attempt == MAX_RETRIES) {
                    throw new ExternalApiException(
                            "Przekroczono limit zapytań Open AQ API (429) po " + MAX_RETRIES + " próbach", e);
                }
                long waitSeconds = resolveWaitSeconds(e.getResponseHeaders());
                log.warn("HTTP 429 z Open AQ API, próba {}/{}, czekam {}s", attempt, MAX_RETRIES, waitSeconds);
                sleep(waitSeconds);
            } catch (RestClientException e) {
                throw new ExternalApiException("Nie udało się pobrać danych z Open AQ API", e);
            }
        }
        throw new ExternalApiException("Nie udało się pobrać danych z Open AQ API po " + MAX_RETRIES + " próbach");
    }

    private long resolveWaitSeconds(HttpHeaders headers) {
        String reset = headers != null ? headers.getFirst("X-Ratelimit-Reset") : null;
        try {
            return reset != null ? Long.parseLong(reset) : DEFAULT_RETRY_WAIT_SECONDS;
        } catch (NumberFormatException e) {
            return DEFAULT_RETRY_WAIT_SECONDS;
        }
    }

    private void sleep(long seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ExternalApiException("Przerwano oczekiwanie na limit Open AQ API", e);
        }
    }
}

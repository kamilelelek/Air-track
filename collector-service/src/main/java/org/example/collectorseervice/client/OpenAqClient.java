package org.example.collectorseervice.client;

import org.example.collectorseervice.dto.latest.LatestReadingDto;
import org.example.collectorseervice.dto.locations.LocationDto;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Component
public class OpenAqClient {
    OpenAqConfig openAqConfig;

    OpenAqClient(OpenAqConfig openAqConfig) {
        this.openAqConfig = openAqConfig;
    }

    public List<LocationDto> getLocationResponse() {
        LocationsResponse response;
        try {
            response = openAqConfig.openAqWebClient().get()
                    .uri("/locations?iso=PL")
                    .retrieve()
                    .body(LocationsResponse.class);
        } catch (RestClientException e) {
            throw new ExternalApiException("Nie udało się pobrać danych z Open AQ API", e);
        }

        if (response == null || response.results() == null) {
            throw new ExternalApiException("Open Aq API zwróciło pustą odpowiedź");
        }
        return response.results();
    }
    public List<LatestReadingDto> getLatestResponse(LocationDto location) {
        LatestResponse response;
        try{
            response= openAqConfig.openAqWebClient().get()
                    .uri("/locations/"+ location.id()+"/latest")
                    .retrieve()
                    .body(LatestResponse.class);
        }catch(RestClientException e){
            throw new ExternalApiException("Nie udało się pobrać danych z Open AQ API",e);
        }
        if (response == null || response.results() == null) {
            throw new ExternalApiException("Open Aq API zwróciło pustą odpowiedź");
        }
        return response.results();
    }
}
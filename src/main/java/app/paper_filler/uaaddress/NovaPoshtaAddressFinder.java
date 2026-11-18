package app.paper_filler.uaaddress;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NovaPoshtaAddressFinder implements UaAddressFinder {

    private static final Logger log = LoggerFactory.getLogger(NovaPoshtaAddressFinder.class);

    private final RestClient rest = RestClient.create("https://api.novaposhta.ua/v2.0/json/");
    private final ObjectMapper mapper;
    private final String apiKey;

    public NovaPoshtaAddressFinder(ObjectMapper mapper,
                                   @Value("${app.nova-poshta-key:}") String apiKey){
        this.mapper = mapper;
        this.apiKey = apiKey;
        if(apiKey.isBlank()){
            log.warn("NOVA_POSHTA_API_KEY is not set — Ukrainian address suggestions are off");
        }
    }
    @Override
    public List<UaCitySuggestion> findCities(String query) {
        if(apiKey.isBlank() || query == null || query.trim().length() < 2) return List.of();

        String json = call("searchSettlements", Map.of("CityName",query.trim(),"Limit","10"));
        List<UaCitySuggestion> result = new ArrayList<>();
        try{
            CitiesResponse response = mapper.readValue(json,CitiesResponse.class);
            if(response.success() && !response.data().isEmpty()){
                for(NpCity c : response.data().get(0).addresses()){
                    result.add(new UaCitySuggestion(c.ref(),c.present(),c.mainDescription(),c.area(),c.streetsAvailability()));
                }
            }
        } catch (Exception e){
            log.warn("Nova Poshta cities failed for '{}': {}", query, e.getMessage());
        }
        return result;
    }

    @Override
    public List<UaStreetSuggestion> findStreets(String cityRef, String query) {
        if(apiKey.isBlank() || cityRef == null || query == null || query.trim().length() <2) return List.of();

        String json = call("searchSettlementStreets",
                Map.of("SettlementRef", cityRef, "StreetName", query.trim(), "Limit", "10"));
        List<UaStreetSuggestion> result = new ArrayList<>();
        try{
            StreetsResponse response = mapper.readValue(json, StreetsResponse.class);
            if(response.success() && !response.data().isEmpty()){
                for(NpStreet s : response.data().get(0).addresses()){
                    result.add(new UaStreetSuggestion(s.ref(), s.type(), s.name()));
                }
            }
        }catch (Exception e) {
            log.warn("Nova Poshta streets failed for '{}': {}", query, e.getMessage());
        }
        return result;
    }

    private String call(String method, Map<String,String> properties) {
        try{
            String body = mapper.writeValueAsString(Map.of(
                    "apiKey",apiKey,
                    "modelName","Address",
                    "calledMethod",method,
                    "methodProperties", properties));
            return rest.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(String.class);
        }catch (Exception e) {
            log.warn("Nova Poshta call {} failed: {}", method, e.getMessage());
            return "{}";
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record CitiesResponse(boolean success, List<CitiesData> data) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record CitiesData(@JsonProperty("Addresses") List<NpCity> addresses) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record NpCity(@JsonProperty("Ref") String ref,
                  @JsonProperty("Present") String present,
                  @JsonProperty("MainDescription") String mainDescription,
                  @JsonProperty("Area") String area,
                  @JsonProperty("StreetsAvailability") boolean streetsAvailability) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record StreetsResponse(boolean success, List<StreetsData> data) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record StreetsData(@JsonProperty("Addresses") List<NpStreet> addresses) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record NpStreet(@JsonProperty("SettlementStreetRef") String ref,
                    @JsonProperty("StreetsTypeDescription") String type,
                    @JsonProperty("SettlementStreetDescription") String name) {}
}

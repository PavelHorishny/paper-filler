package app.paper_filler.postcode;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Service
public class UugPostcodeFinder implements PostcodeFinder{

    private static final Logger log  = LoggerFactory.getLogger(UugPostcodeFinder.class);

    private final RestClient rest;
    private final ObjectMapper mapper;

    public UugPostcodeFinder(RestClient.Builder builder, ObjectMapper mapper){
        this.rest = builder.baseUrl("https://services.gugik.gov.pl/uug/").build();
        this.mapper = mapper;
    }
    @Override
    public String findPostcode(String city, String street, String house) {
        String address = city + ", "+street+" "+house;
        try{
            String json = rest.get()
                    .uri(uri-> uri
                            .queryParam("request","GetAddress")
                            .queryParam("address","{address}")
                            .build(address))
                    .retrieve()
                    .body(String.class);
            UugResponse response = mapper.readValue(json,UugResponse.class);
            if (response.results() == null) {
                return "";
            }
            for (UugResult r : response.results.values()){
                if("1".equals(r.accuracy()) && r.code() != null){
                    return r.code();
                }
            }
        }catch (Exception e){
            log.warn("UUG lookup failed for '{}': {}", address,e.getMessage());
        }
        return "";
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    record UugResponse(Map<String,UugResult> results) {}
    @JsonIgnoreProperties(ignoreUnknown = true)
    record UugResult(String code,String teryt, String accuracy) {}

}

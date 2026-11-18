package app.paper_filler.uaaddress;

import java.util.List;

public interface UaAddressFinder {
    List<UaCitySuggestion> findCities(String query);
    List<UaStreetSuggestion> findStreets(String cityRef, String query);
}

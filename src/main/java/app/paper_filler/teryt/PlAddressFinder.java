package app.paper_filler.teryt;

import java.util.List;

public interface PlAddressFinder {
    List<CitySuggestion> findCities(String query);
    List<StreetSuggestion> findStreets(String citySym, String query);
}

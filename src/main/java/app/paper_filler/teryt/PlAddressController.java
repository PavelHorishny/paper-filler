package app.paper_filler.teryt;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pl")
@RequiredArgsConstructor
public class PlAddressController {

    private final PlAddressFinder finder;

    @GetMapping("/cities")
    public List<CitySuggestion> cities(@RequestParam String q) {
        return finder.findCities(q);
    }

    @GetMapping("/streets")
    public List<StreetSuggestion> streets(@RequestParam String sym, @RequestParam String q) {
        return finder.findStreets(sym, q);
    }
}

package app.paper_filler.uaaddress;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ua")
@RequiredArgsConstructor
public class UaAddressController {
    private final UaAddressFinder finder;

    @GetMapping("/cities")
    public List<UaCitySuggestion> cities(@RequestParam String q){
        return finder.findCities(q);
    }

    @GetMapping("/streets")
    public List<UaStreetSuggestion> streets (@RequestParam String ref,
                                             @RequestParam String q){
        return finder.findStreets(ref,q);
    }
}

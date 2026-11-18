package app.paper_filler.postcode;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PostcodeController {
    private final PostcodeFinder finder;

    @GetMapping("api/pl/postcode")
    public String postcode(@RequestParam String city,
                           @RequestParam String street,
                           @RequestParam String house) {
        return finder.findPostcode(city,street,house);
    }
}

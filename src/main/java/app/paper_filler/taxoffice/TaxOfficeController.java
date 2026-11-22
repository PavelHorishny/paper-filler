package app.paper_filler.taxoffice;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TaxOfficeController {
    private final TaxOfficeFinder finder;

    @GetMapping("/api/pl/tax-office")
    public String taxOffice(@RequestParam String sym,
                            @RequestParam(required = false) String house){
        TaxOffice office = finder.find(sym,house);
        return office == null ? "" : office.name();
    }

}

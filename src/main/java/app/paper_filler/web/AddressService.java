package app.paper_filler.web;

import app.paper_filler.core.translit.TransliterationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final TransliterationService transliterationService;
    public Address fillAddress(Address address, Citizenship citizenship) {
    // Address filledAddress = new Address();
    return Address.builder()
        .region(transliterationService.transliterate(address.getRegion(), citizenship))
        .commune(transliterationService.transliterate(address.getCity(), citizenship))
        .district(transliterationService.transliterate(address.getCity(), citizenship))
        .city(transliterationService.transliterate(address.getCity(), citizenship))
        .street(transliterationService.transliterate(address.getStreet(), citizenship))
        .house(address.getHouse())
        .apartment(address.getApartment())
        .postalCode(address.getPostalCode())
        .build();
    }
}

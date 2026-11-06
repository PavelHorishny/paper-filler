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
        .country(citizenship.getCountry())
        .wojewodztwo(transliterationService.transliterate(address.getWojewodztwo(), citizenship))
        .gmina(transliterationService.transliterate(address.getCity(), citizenship))
        .powiat(transliterationService.transliterate(address.getCity(), citizenship))
        .city(transliterationService.transliterate(address.getCity(), citizenship))
        .street(transliterationService.transliterate(address.getStreet(), citizenship))
        .house(address.getHouse())
        .apartment(address.getApartment())
        .postalCode(address.getPostalCode())
        .build();
    }
}

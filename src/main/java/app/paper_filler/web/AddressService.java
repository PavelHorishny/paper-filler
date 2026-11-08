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

    public Address fillHomeAddress(Address address, Citizenship citizenship) {
        Address filled = new Address();
        if(address.getRegion() != null && !address.getRegion().isEmpty()) filled.setRegion(transliterationService.transliterate(address.getRegion(), citizenship));
        if(address.getDistrict() != null && !address.getDistrict().isEmpty()){
            filled.setDistrict(transliterationService.transliterate(address.getDistrict(), citizenship));
        }else {
            filled.setDistrict(transliterationService.transliterate(address.getCity(), citizenship));
        }
        if(address.getCommune() != null && !address.getCommune().isEmpty()){
            filled.setCommune(transliterationService.transliterate(address.getCommune(), citizenship));
        }else {
            filled.setCommune(transliterationService.transliterate(address.getCity(), citizenship));
        }
        if(address.getStreet() != null && !address.getStreet().isEmpty()) filled.setStreet(transliterationService.transliterate(address.getStreet(), citizenship));
        filled.setHouse(address.getHouse());
        filled.setApartment(address.getApartment());
        filled.setCity(transliterationService.transliterate(address.getCity(), citizenship));
        filled.setPostalCode(address.getPostalCode());
        return filled;
    }

    public Address fillLocalAddress(Address address) {
        Address filled = new Address();
        if(address.getRegion() != null && !address.getRegion().isEmpty()){
            filled.setRegion(transliterationService.transliterate(address.getRegion(), Citizenship.PL));
        }else {
            filled.setRegion("Zachodniopomorskie");
        }
        if(address.getDistrict() != null && !address.getDistrict().isEmpty()){
            filled.setDistrict(transliterationService.transliterate(address.getDistrict(), Citizenship.PL));
        }else {
            filled.setDistrict(transliterationService.transliterate(address.getCity(), Citizenship.PL));
        }
        if(address.getCommune() != null && !address.getCommune().isEmpty()){
            filled.setCommune(transliterationService.transliterate(address.getCommune(), Citizenship.PL));
        }else {
            filled.setCommune(transliterationService.transliterate(address.getCity(), Citizenship.PL));
        }
        if(address.getStreet() != null && !address.getStreet().isEmpty()) filled.setStreet(transliterationService.transliterate(address.getStreet(), Citizenship.PL));
        filled.setHouse(address.getHouse()!= null ? address.getHouse() : "");
        filled.setApartment(address.getApartment()!= null ? address.getApartment() : "");
        filled.setCity(transliterationService.transliterate(address.getCity(), Citizenship.PL));
        filled.setPostalCode(address.getPostalCode()!= null ? address.getPostalCode() : "");
        return filled;
    }
}

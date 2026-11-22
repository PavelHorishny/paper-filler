package app.paper_filler.form;

import app.paper_filler.taxoffice.TaxOffice;

public record CompanyAddress(
        String id,
        String label,
        String region,
        String district,
        String commune,
        String city,
        String street,
        String house,
        String apartment,
        String postalCode,
        TaxOffice taxOffice,
        boolean needsApartment
) {
    public Address toAddress(String apartmentFromForm) {
        String apt = needsApartment ? apartmentFromForm : apartment;
        return Address.builder()
                .region(region)
                .district(district)
                .commune(commune)
                .city(city)
                .street(street)
                .house(house)
                .apartment(apt)
                .postalCode(postalCode)
                .build();
    }
}

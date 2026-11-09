package app.paper_filler.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    private String region;
    private String district;
    private String commune;
    private String street;
    private String house;
    private String apartment;
    private String city;
    private String postalCode;


    public String shortAddress() {
        String apt = isBlank(apartment) ? "" : " m." + apartment;
        return (s(street) + " " + s(house) + apt + ", " + s(postalCode) + " " + s(city)).strip();
    }

    private static String s(String v) {
        return v == null ? "" : v;
    }

    private static boolean isBlank(String v) {
        return v == null || v.isBlank();
    }
}

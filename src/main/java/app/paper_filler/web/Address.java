package app.paper_filler.web;

import lombok.Data;

@Data
public class Address {
    private String country;
    private String wojewodztwo;
    private String powiat;
    private String gmina;
    private String street;
    private String house;
    private String apartment;
    private String city;
    private String postalCode;
}

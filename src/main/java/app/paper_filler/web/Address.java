package app.paper_filler.web;

import lombok.Data;

@Data
public class Address {
    private String country;
    private String region;
    private String district;
    private String city;
    private String street;
    private String house;
    private String apartment;
    private String postalCode;
}

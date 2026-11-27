package app.paper_filler.documents;

public enum Tags {

    NAME ("name"),
    SURNAME ("surname"),
    FULFILMENT_DATE ("fulfilmentDate"),
    LOCATION ("location"),
    PESEL ("pesel"),
    HOME_ADDRESS ("homeAddress"),
    LOCAL_ADDRESS ("localAddress"),
    VIBER ("viber"),
    PHONE ("tel"),
    MAIL ("email"),
    BIRTH_DATE ("birthDate"),
    BIRTH_PLACE ("birthPlace"),
    CITIZENSHIP("label"),
    PASSPORT_SERIES("series"),
    PASSPORT_NUMBER("number"),
    LOCAL_REGION("locRegion"),
    HOME_REGION("homeRegion"),
    LOCAL_DISTRICT("localDistrict"),
    HOME_DISTRICT("homeDistrict"),
    LOCAL_COMMUNE("locCommune"),
    HOME_COMMUNE("homeCommune"),
    LOCAL_STREET("locStreet"),
    HOME_STREET ("homeStreet"),
    LOCAL_HOUSE("locHouse"),
    HOME_HOUSE ("homeHouse"),
    LOCAL_APARTMENT ("locApartment"),
    HOME_APARTMENT ("homeApartment"),
    LOCAL_POSTAL_CODE ("locPostal"),
    HOME_POSTAL_CODE ("homePostal"),
    LOCAL_CITY_LABEL ("lCityLab"),
    LOCAL_CITY ("locCity"),
    HOME_CITY_LABEL ("hCityLab"),
    HOME_CITY ("homeCity"),
    TAX_OFFICE ("taxOffice"),
    BANK_ACCOUNT ("account"),
    SCHOOL_NAME ("schoolName"),
    MONTH ("month"),
    ROLE ("stanowisko"),
    BANK_NAME ("bank");

    final String value;

    Tags (String value){
        this.value = value;
    }

}

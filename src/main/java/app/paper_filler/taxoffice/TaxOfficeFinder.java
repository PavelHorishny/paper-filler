package app.paper_filler.taxoffice;

public interface TaxOfficeFinder {
    TaxOffice find(String streetSym, String house);
}

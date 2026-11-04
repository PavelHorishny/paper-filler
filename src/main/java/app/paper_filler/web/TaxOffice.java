package app.paper_filler.web;

import lombok.Getter;

@Getter
public enum TaxOffice {
    FIRST ("1_st", "Pierwszy Urząd Skarbowy w Szczecinie ul. Michała Drzymały 5, 70-217 Szczecin"),
    SECOND ("2_nd","Drugi Urząd Skarbowy w Szczecinie ul. Wacława Felczaka 19, 71-417 Szczecin"),
    THIRD ("3_rd", "Trzeci Urząd Skarbowy w Szczecinie ul. Lucjana Rydla 65, 70-783 Szczecin");

    private final String label;
    private final String name;

    TaxOffice (String label, String name){
        this.label = label;
        this.name = name;
    }

}

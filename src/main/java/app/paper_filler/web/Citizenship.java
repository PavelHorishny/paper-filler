package app.paper_filler.web;

import lombok.Getter;

@Getter
public enum Citizenship {
    PL("PL", "Polska","[A-Z]{3}", "\\d{6}"),
    UA("UA", "Ukraina","[A-Z]{2}", "\\d{6}"),
    BY("BY", "Białoruska","[A-Z]{2}", "\\d{7}"), //check data
    MD("MD", "Mołdawia","[A-Z]{2}", "\\d{7}"); //check data

    private final String label;
    private final String country;
    private final String seriesPattern;
    private final String numberPattern;

    Citizenship(String label, String country, String seriesPattern,String numberPattern) {
        this.label = label;
        this.country = country;
        this.seriesPattern = seriesPattern;
        this.numberPattern = numberPattern;
    }

}

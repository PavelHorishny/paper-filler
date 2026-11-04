package app.paper_filler.web;

import lombok.Getter;

@Getter
public enum Citizenship {
    PL("PL", "[A-Z]{3}", "\\d{6}"),
    UA("UA", "[A-Z]{2}", "\\d{6}"),
    BY("BY", "[A-Z]{2}", "\\d{7}"), //check data
    MD("MD", "[A-Z]{2}", "\\d{7}"); //check data

    private final String label;
    private final String seriesPattern;
    private final String numberPattern;

    Citizenship(String label, String seriesPattern,String numberPattern) {
        this.label = label;
        this.seriesPattern = seriesPattern;
        this.numberPattern = numberPattern;
    }

}

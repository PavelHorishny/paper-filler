package app.paper_filler.web;

import lombok.Getter;

@Getter
public enum Citizenship {
    PL("PL"),UA("UA"),BY("BY"),MD("MD");

    private final String label;

    Citizenship(String label) {
        this.label = label;
    }

}

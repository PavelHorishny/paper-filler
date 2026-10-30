package app.paper_filler.web;

public enum Citizenship {
    PL("PL"),UA("UA"),BY("BY"),MD("MD");

    private final String label;

    Citizenship(String label) {
        this.label = label;
    }

    public String getLabel(){
        return label;
    }
}

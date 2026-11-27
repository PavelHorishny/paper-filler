package app.paper_filler.documents;

import lombok.Getter;

@Getter
public enum TagShortening {
HOME_CITY("homeCity","Miejscowość","Mść","M",24),
LOCAL_CITY("locCity","Miejscowość","Mść","M",24);

final String tag;
final String full;
final String shorter;
final String shortest;
final int maxLength;

    TagShortening(String tag, String full, String shorter, String shortest, int maxLength) {
        this.tag = tag;
        this.full = full;
        this.shorter = shorter;
        this.shortest = shortest;
        this.maxLength = maxLength;
    }
}

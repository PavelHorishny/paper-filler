package app.paper_filler.documents;

import lombok.Getter;

@Getter
public enum TagShortening {
HOME_CITY("homeCity","Miejscowość","Mść","M",24,Tags.HOME_CITY),
LOCAL_CITY("locCity","Miejscowość","Mść","M",24, Tags.LOCAL_CITY);

final String tag;
final String full;
final String shorter;
final String shortest;
final int maxLength;
Tags tags;

    TagShortening(String tag, String full, String shorter, String shortest, int maxLength, Tags tags) {
        this.tag = tag;
        this.full = full;
        this.shorter = shorter;
        this.shortest = shortest;
        this.maxLength = maxLength;
        this.tags = tags;
    }

    TagShortening shortening(Tags tags){
        return
    }
}

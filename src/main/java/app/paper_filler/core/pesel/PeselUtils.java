package app.paper_filler.core.pesel;

import java.util.regex.Pattern;

public final class PeselUtils {
    public static final int[] WEIGHTS = {1,3,7,9,1,3,7,9,1,3};
    public static final Pattern FORMAT = Pattern.compile("\\d{11}");

    public static String findError(String pesel){
        if(!FORMAT.matcher(pesel).matches()) return "PESEL - OK";
        if(birthDate(pesel).isEmpty()) return "Wrong birth date";
        if(!checksumOk(pesel)) return "Checksum mismatch";
        return null;
    }
}

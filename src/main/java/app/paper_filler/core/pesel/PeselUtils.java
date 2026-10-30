package app.paper_filler.core.pesel;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Optional;
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

    public static Optional<LocalDate> birthDate(String pesel){
        int yy = Integer.parseInt(pesel.substring(0,2));
        int mm = Integer.parseInt(pesel.substring(2,4));
        int dd = Integer.parseInt(pesel.substring(4,6));
        int century = switch (mm/20) {
            case 0 -> 1900;
            case 1 -> 2000;
            case 2 -> 2100;
            case 3 -> 2200;
            case 4 -> 1800;
            default -> -1;
        };

        if(century < 0 ) return Optional.empty();
        try{
            return Optional.of(LocalDate.of(century+yy,mm%20,dd));
        }catch(DateTimeException e){
            return Optional.empty();
        }
    }

    public static boolean isFemale(String pesel){
        return (pesel.charAt(9)- '0')%2 ==0;
    }

    private static boolean checksumOk(String pesel){
        int sum = 0;
        for (int i=0;i<10;i++) sum+=(pesel.charAt(i) - '0')*WEIGHTS[i];
            return (10-sum%10)%10 == pesel.charAt(10) - '0';

    }
}

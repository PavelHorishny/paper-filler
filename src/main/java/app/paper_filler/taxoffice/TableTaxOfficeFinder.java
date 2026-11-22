package app.paper_filler.taxoffice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TableTaxOfficeFinder implements TaxOfficeFinder{
    private static final Logger log = LoggerFactory.getLogger(TableTaxOfficeFinder.class);
    private static final Pattern HOUSE_NUMBER = Pattern.compile("^\\s*(\\d+)");



    record Rule(String houses, TaxOffice office) {}

    private final Map<String, List<Rule>> rulesByStreet = new HashMap<>();


    public TableTaxOfficeFinder(@Value("${app.tax-offices-file:./config/teryt/szczecin-tax-offices.csv}") Path file) throws IOException {
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        for(int i = 1; i<lines.size(); i++){
            String line = lines.get(i);
            if(line.isBlank()) continue;
            String[] c = line.split(";",-1);
            rulesByStreet.computeIfAbsent(c[0], k-> new ArrayList<>())
                    .add(new Rule(c[2],TaxOffice.valueOf(c[3])));
        }
        log.info("Tax offices loaded: {} streets", rulesByStreet.size());
    }


    @Override
    public TaxOffice find(String streetSym, String house) {
        List<Rule> rules = rulesByStreet.get(streetSym);
        if(rules==null){
            return null;
        }
        if(rules.size() == 1 && rules.getFirst().houses().isEmpty()){
            return rules.getFirst().office();
        }

        Integer n = houseNumber(house);
        if(n==null) return null;

        TaxOffice others = null;
        for(Rule r: rules) {
            switch (r.houses()){
                case "*" -> others = r.office();
                case "?","" -> { }
                default -> {
                    if(matches(r.houses(),n)) return r.office();
                }
            }
        }
        return others;
    }

    static Integer houseNumber(String house){
        if(house == null) return null;
        Matcher m = HOUSE_NUMBER.matcher(house);
        return m.find() ? Integer.parseInt(m.group(1)) : null;
    }

    static boolean matches (String houses, int n) {
        for (String token:houses.split(",")){
            boolean evenOnly = token.endsWith("p");
            if(evenOnly) {
                token = token.substring(0, token.length() - 1);
                if(n%2!=0) continue;
            }
            int from;
            int to;
            if(token.contains("-")){
                String [] p = token.split("-",-1);
                from = Integer.parseInt(p[0]);
                to = p[1].isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(p[1]);
            }else {
                from = Integer.parseInt(token);
                to= from;
            }
            if(n>=from&&n<=to) return true;
        }
        return false;
    }
}

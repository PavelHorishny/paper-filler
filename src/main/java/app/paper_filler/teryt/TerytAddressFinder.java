package app.paper_filler.teryt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.*;

@Service
public class TerytAddressFinder implements PlAddressFinder {

    private static final Logger log = LoggerFactory.getLogger(TerytAddressFinder.class);
    private static final int LIMIT = 10;

    private final List<CitySuggestion> cities = new ArrayList<>();
    private final Map<String, List<StreetSuggestion>> streetsByCity = new HashMap<>();

    public TerytAddressFinder(@Value("${app.teryt-dir:./config/teryt}") Path dir) throws IOException {
        Map<String, String> names = loadTerc(dir.resolve("TERC_32.csv"));
        loadSimc(dir.resolve("SIMC_32.csv"), names);
        loadUlic(dir.resolve("ULIC_32.csv"));
        log.info("TERYT loaded: {} cities, {} cities with streets", cities.size(), streetsByCity.size());
    }

    // ---------- загрузка ----------

    /** TERC: код → название. "32" = województwo, "3201" = powiat, "3201011" = gmina */
    private Map<String, String> loadTerc(Path file) throws IOException {
        Map<String, String> names = new HashMap<>();
        for (String[] c : readCsv(file)) {
            String code = c[0] + c[1] + c[2] + c[3];   // пустые части просто не добавляют цифр
            names.put(code, c[4]);
        }
        return names;
    }

    /** SIMC: населённые пункты */
    private void loadSimc(Path file, Map<String, String> names) throws IOException {
        for (String[] c : readCsv(file)) {
            String sym = c[7];
            String sympod = c[8];
            if (!sym.equals(sympod)) {
                continue;   // пропускаем части сёл и районы городов — нужны только сами пункты
            }
            String gmina = names.get(c[0] + c[1] + c[2] + c[3]);
            String powiat = names.get(c[0] + c[1]);
            cities.add(new CitySuggestion(sym, c[6], gmina, powiat));
        }
    }

    /** ULIC: улицы, сгруппированные по коду населённого пункта */
    private void loadUlic(Path file) throws IOException {
        for (String[] c : readCsv(file)) {
            String sym = c[4];
            String name = (c[8] + " " + c[7]).strip();   // NAZWA_2 + NAZWA_1: "Marcina Borzymowskiego"
            streetsByCity.computeIfAbsent(sym, k -> new ArrayList<>())
                    .add(new StreetSuggestion(c[5], c[6], name));
        }
    }

    private List<String[]> readCsv(Path file) throws IOException {
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        List<String[]> rows = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {           // строка 0 — заголовки
            String line = lines.get(i);
            if (line.isBlank()) continue;
            rows.add(line.split(";", -1));                  // -1: не терять пустые колонки в конце
        }
        return rows;
    }

    // ---------- поиск ----------

    @Override
    public List<CitySuggestion> findCities(String query) {
        String q = normalize(query);
        if (q.length() < 2) return List.of();

        List<CitySuggestion> result = new ArrayList<>();
        for (CitySuggestion c : cities) {
            if (normalize(c.name()).startsWith(q)) {
                result.add(c);
            }
        }
        result.sort(Comparator.comparingInt(c -> c.name().length()));   // "Szczecin" раньше "Szczecinek"
        return result.size() > LIMIT ? result.subList(0, LIMIT) : result;
    }

    @Override
    public List<StreetSuggestion> findStreets(String citySym, String query) {
        String q = normalize(query);
        if (q.length() < 2) return List.of();

        List<StreetSuggestion> result = new ArrayList<>();
        for (StreetSuggestion s : streetsByCity.getOrDefault(citySym, List.of())) {
            if (normalize(s.name()).contains(q)) {
                result.add(s);
                if (result.size() == LIMIT) break;
            }
        }
        return result;
    }

    /** "Łódź" → "lodz": без регистра и без польских знаков, чтобы искать как удобно */
    static String normalize(String s) {
        if (s == null) return "";
        String lower = s.toLowerCase(Locale.ROOT).replace('ł', 'l');
        return Normalizer.normalize(lower, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .strip();
    }
}

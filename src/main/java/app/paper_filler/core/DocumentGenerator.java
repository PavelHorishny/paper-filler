package app.paper_filler.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import app.paper_filler.config.AppProperties;
import app.paper_filler.config.DocumentDef;
import app.paper_filler.core.translit.TransliterationService;
import app.paper_filler.web.ClientForm;
import com.deepoove.poi.XWPFTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class DocumentGenerator {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final AppProperties properties;
    private final TransliterationService translit;

    private static final DateTimeFormatter MONTH = DateTimeFormatter.ofPattern("MMMM", Locale.forLanguageTag("pl"));

    public void generate (ClientForm form, List<String> docIds)  throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("name", form.getName());
        data.put("surname", form.getSurname());
        data.put("fulfilmentDate", formatDate(form.getFulfilmentDate()));
        data.put("location", "Szczecin");
        data.put("pesel", form.getPesel());
        data.put("homeAddress", form.getHomeAddress().shortAddress());
        data.put("localAddress", form.getLocalAddress().shortAddress());
        data.put("viber", text(form.getViber()));
        data.put("tel", text(form.getPhoneNumber()));
        data.put("birthDate", formatDate(form.getBirthDate()));
        data.put("birthPlace", text(form.getBirthPlace()));
        data.put("label", form.getCitizenship().getCountry());
        data.put("series", form.getPassport().getSeries());
        data.put("number", form.getPassport().getNumber());
        data.put("locRegion", form.getLocalAddress().getRegion());
        data.put("homeRegion", form.getHomeAddress().getRegion());
        data.put("locDistrict", form.getLocalAddress().getDistrict());
        data.put("homeDistrict", form.getHomeAddress().getDistrict());
        data.put("locCommune", form.getLocalAddress().getCommune());
        data.put("homeCommune", form.getHomeAddress().getCommune());
        data.put("locStreet", form.getLocalAddress().getStreet());
        data.put("homeStreet", form.getHomeAddress().getStreet());
        data.put("locHouse", form.getLocalAddress().getHouse());
        data.put("homeHouse", form.getHomeAddress().getHouse());
        data.put("locApartment", form.getLocalAddress().getApartment());
        data.put("homeApartment", form.getHomeAddress().getApartment());
        data.put("locPostal", form.getLocalAddress().getPostalCode());
        data.put("homePostal", form.getHomeAddress().getPostalCode());
        data.put("locCity", form.getLocalAddress().getCity());
        data.put("homeCity", form.getHomeAddress().getCity());
        data.put("taxOffice", form.getTaxOffice().getAddress());
        data.put("account", form.getAccount());
        data.put("schoolName", text(form.getSchoolName()));
        data.put("month", form.getFulfilmentDate().format(MONTH));
        data.put("stanowisko", "magazynier");
        data.put("bank", "");

        Files.createDirectories(properties.outputDir());
        for (DocumentDef doc : properties.documents()){
            if(!docIds.contains((doc.id()))) continue;

            Path template = properties.templatesDir().resolve(doc.template());
            Path output = properties.outputDir().resolve(doc.id() + ".docx");
            XWPFTemplate.compile(template.toString())
                    .render(data)
                    .writeToFile(output.toString());
        }
    }

    private static String text(String value){
        return value == null ? "" : value.strip();
    }

    private static String formatDate(LocalDate date){
        return date == null ? "": date.format(DATE);
    }

    private static String fullName(ClientForm form){
        return (text(form.getName()) + " " + text(form.getSurname())).strip();
    }
}

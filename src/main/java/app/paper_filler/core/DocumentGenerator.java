package app.paper_filler.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import app.paper_filler.web.ClientForm;
import com.deepoove.poi.XWPFTemplate;
import org.springframework.stereotype.Service;

@Service
public class DocumentGenerator {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public void generate (ClientForm form) throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("imie", form.getName());
        data.put("nazwisko", form.getSurname());
        data.put("imie_nazwisko", fullName(form));
        data.put("data_urodzenia", form.getBirthDate());
        data.put("telefon", text(form.getPhoneNumber()));
        data.put("data_urodzenia", formatDate(form.getBirthDate()));
        data.put("data_dokumentu",formatDate(form.getFulfilmentDate()));
        data.put("series", form.getPassport().getSeries());
        data.put("number", form.getPassport().getNumber());
        
        Files.createDirectories(Path.of("output"));
        XWPFTemplate.compile("templates/umowa.docx")
            .render(data)
            .writeToFile("output/umowa.docx");
    }

    private static String text(String value){
        return value == null ? "" : value.strip();
    }

    private static String formatDare(LocalDate date){
        return date == null ? "": date.format(DATE);
    }

    private static String fullName(ClientForm form){
        return (text(form.getName()) + " " + text(form.getSurname())).strip();
    }
}

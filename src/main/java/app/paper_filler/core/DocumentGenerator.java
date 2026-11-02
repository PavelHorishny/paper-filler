package app.paper_filler.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import app.paper_filler.web.ClientForm;
import com.deepoove.poi.XWPFTemplate;
import org.springframework.stereotype.Service;

@Service
public class DocumentGenerator {

    public void generate (ClientForm form) throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("imie_nazwisko", form.getName()+" "+form.getSurname());
        data.put("data_urodzenia", form.getBirthDate());
        data.put("telefon", form.getPhoneNumber());

        Files.createDirectories(Path.of("output"));
        XWPFTemplate.compile("templates/umowa.docx")
            .render(data)
            .writeToFile("output/umowa.docx");
    }
}

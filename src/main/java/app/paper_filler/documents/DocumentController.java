package app.paper_filler.documents;

import app.paper_filler.config.AppProperties;
import app.paper_filler.form.ClientForm;
import app.paper_filler.form.FormSession;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentGenerator generator;
    private final AppProperties properties;

    @GetMapping("/documents")
    public String showDocuments(Model model, HttpSession session) {
        ClientForm form = FormSession.get(session);
        if(form == null) return "redirect:/";
        model.addAttribute("form",form);
        model.addAttribute("documents", properties.documents());
        return "documents";
    }

    @PostMapping("/documents")
    public ResponseEntity<byte[]> generate(@RequestParam(name = "docs", required = false) List<String> docs, HttpSession session) throws IOException {

        ClientForm form = FormSession.get(session);
        if (form == null || docs == null || docs.isEmpty()){
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/documents")).build();
        }
        byte [] zip = generator.generateZip(form,docs);
        String fileName = form.getSurname() + "_" + form.getName() + "_" + LocalDate.now() + ".zip";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(fileName, StandardCharsets.UTF_8).build().toString())
                .contentType(MediaType.parseMediaType("application/zip"))
                .body(zip);
    }
}

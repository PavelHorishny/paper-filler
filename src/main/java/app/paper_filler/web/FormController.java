package app.paper_filler.web;

import app.paper_filler.config.AppProperties;
import app.paper_filler.core.translit.TransliterationService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import app.paper_filler.core.DocumentGenerator;
import org.springframework.web.bind.annotation.RequestParam;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class FormController {

    private static final String FORM = "clientForm";

    private final DocumentGenerator generator;
    private final AppProperties properties;
    private final JsonMapper jsonMapper;
    private final AddressService addressService;
    private final TransliterationService transliterationService;

    private Path formFile(){
        return properties.cacheDir().resolve("last-form.json");
    }

    @GetMapping ("/")
    public String newForm(Model model, HttpSession session){
        session.removeAttribute(FORM);
        return showForm(new ClientForm(), model);
    }

    @GetMapping("/edit")
    public String editForm(Model model, HttpSession session){
        ClientForm saved = (ClientForm) session.getAttribute(FORM);
        if(saved == null) return "redirect:/";
        return showForm(saved,model);
    }
    private String showForm(ClientForm form, Model model) {
        model.addAttribute(FORM,form);
        model.addAttribute("citizenships", Citizenship.values());
        model.addAttribute("taxOffices", TaxOffice.values());
        return "form";
    }

    @PostMapping("/")
    public String submitForm(@ModelAttribute ClientForm clientForm, HttpSession session) {
        Citizenship c = clientForm.getCitizenship();
        clientForm.setFulfilmentDate(LocalDate.now());
        clientForm.setBirthPlace(transliterationService.transliterate(clientForm.getBirthPlace(),c));
        clientForm.setSchoolName(transliterationService.transliterate(clientForm.getSchoolName(),c));
        clientForm.setHomeAddress(addressService.fillHomeAddress(clientForm.getHomeAddress(), c));
        clientForm.setLocalAddress(addressService.fillLocalAddress(clientForm.getLocalAddress()));

        session.setAttribute(FORM,clientForm);
        return "redirect:/documents";
    }

    @GetMapping("/documents")
    public String showDocuments(Model model, HttpSession session) {
        ClientForm form = (ClientForm) session.getAttribute(FORM);
        if(form == null) return "redirect:/";
        model.addAttribute("form",form);
        model.addAttribute("documents", properties.documents());
        return "documents";
    }

    @PostMapping("/documents")
    public ResponseEntity<byte[]> generate(@RequestParam(name = "docs", required = false)List<String> docs, HttpSession session) throws IOException{

        ClientForm form = (ClientForm) session.getAttribute(FORM);
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

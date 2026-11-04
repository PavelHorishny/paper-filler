package app.paper_filler.web;

import app.paper_filler.config.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import app.paper_filler.core.DocumentGenerator;
import org.springframework.web.bind.annotation.RequestParam;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class FormController {

    private final DocumentGenerator generator;
    private final AppProperties properties;
    private final JsonMapper jsonMapper;

    private Path formFile(){
        return properties.cacheDir().resolve("last-form.json");
    }

    @GetMapping ("/")
    public String showForm(Model model) {
        ClientForm form = Files.exists(formFile())
                ? jsonMapper.readValue(formFile().toFile(), ClientForm.class)
                : new ClientForm();
        model.addAttribute("clientForm", form);
        model.addAttribute("citizenships", Citizenship.values());
        model.addAttribute("taxOffices",TaxOffice.values());
        return "form";
    }

    @PostMapping("/")
    public String submitForm(@ModelAttribute ClientForm clientForm) throws IOException {
        
        Files.createDirectories(properties.cacheDir());
        jsonMapper.writeValue(formFile().toFile(), clientForm);
        return "redirect:/documents";
    }

    @GetMapping("/documents")
    public String showDocuments(Model model) {
        if(!Files.exists(formFile())) return "redirect:/";
        model.addAttribute("documents", properties.documents());
        return "documents";
    }

    @PostMapping("/documents")
    public String generate(@RequestParam(name = "docs", required = false)List<String> docs) throws IOException {
        if (!Files.exists(formFile())) return "redirect:/";

        ClientForm form = jsonMapper.readValue(formFile().toFile(), ClientForm.class);
    System.out.println(form);
    System.out.println("Documents: "+docs);
    generator.generate(form,docs == null ? List.of() : docs);

    Files.delete(formFile());

        return "redirect:/";
    }
}

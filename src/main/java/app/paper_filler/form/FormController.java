package app.paper_filler.form;


import app.paper_filler.taxoffice.TaxOffice;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequiredArgsConstructor
public class FormController {

    private final FormService formService;

    private static final String FORM = "clientForm";

    @GetMapping ("/")
    public String newForm(Model model, HttpSession session){
        FormSession.clear(session);
        return showForm(new ClientForm(), model);
    }

    @GetMapping("/edit")
    public String editForm(Model model, HttpSession session){
        ClientForm saved = FormSession.get(session);
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
        formService.prepare(clientForm);
        FormSession.save(session,clientForm);
        return "redirect:/documents";
    }
}

package app.paper_filler.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller 
public class FormController {

    @GetMapping ("/")
    public String showForm(Model model) {
        model.addAttribute("clientForm", new ClientForm());
        model.addAttribute("citizenship", Citizenship.values());
        return "form";
    }

    @PostMapping("/")
    public String submit(@ModelAttribute ClientForm clientForm) {
        System.out.println(clientForm);
        return "redirect:/";
    }
}

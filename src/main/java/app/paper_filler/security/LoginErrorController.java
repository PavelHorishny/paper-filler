package app.paper_filler.security;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginErrorController {

    @GetMapping("/login-error")
    public String loginError() {
        return "login-error";
    }
}

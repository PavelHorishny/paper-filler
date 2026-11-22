package app.paper_filler.security;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionController {

    @GetMapping("/api/ping")
    public String ping(){
        return "ok";
    }
}

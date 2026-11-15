package app.paper_filler.bank;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BankController {

    private final BankFinder bankFinder;

    @GetMapping("/api/bank")
    public String bank(@RequestParam String account){
        return bankFinder.findBank(account);
    }
}

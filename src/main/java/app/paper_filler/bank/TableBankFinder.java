package app.paper_filler.bank;

import app.paper_filler.config.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TableBankFinder implements BankFinder{

    private final AppProperties properties;


    @Override
    public String findBank(String account) {
        if (account == null) return "";
        String digits = account.replaceAll("\\s","");
        if(!digits.matches("\\d{26}")) return "";
        String code = digits.substring(2,5);

        return properties.banks().getOrDefault(code,"");
    }
}

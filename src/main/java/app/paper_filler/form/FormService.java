package app.paper_filler.form;

import app.paper_filler.bank.BankFinder;
import app.paper_filler.translit.TransliterationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class FormService {
    private final AddressService addressService;
    private final TransliterationService transliterationService;
    private final BankFinder bankFinder;

    public void prepare(ClientForm form){
        Citizenship c = form.getCitizenship();
        form.setFulfilmentDate(LocalDate.now());
        form.setBirthPlace(transliterationService.transliterate(form.getBirthPlace(),c));
        form.setSchoolName(transliterationService.transliterate(form.getSchoolName(),c));
        form.setHomeAddress(addressService.fillHomeAddress(form.getHomeAddress(),c));
        form.setLocalAddress(addressService.fillLocalAddress(form.getLocalAddress()));
        if(form.getBank() == null || form.getBank().isBlank()){
            form.setBank(bankFinder.findBank(form.getAccount()));
        }
    }
}

package app.paper_filler.form;

import app.paper_filler.config.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyAddressService {

    private final AppProperties properties;

    public List<CompanyAddress> all(){
        return properties.companyAddresses() == null ? List.of() : properties.companyAddresses();
    }

    public CompanyAddress find(String id){
        for (CompanyAddress a : all()){
            if(a.id().equals(id)){
                return a;
            }
        }
        return null;
    }
}

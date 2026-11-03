package app.paper_filler.web;

import java.time.LocalDate;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data

public class ClientForm {
    private Citizenship citizenship;
    private String name;
    private String surname;
    private String pesel;
    private String passport;
    private String account;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fulfilmentDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDate;
    private String phoneNumber;
    private String viber;
    private String email;
    private String taxOffice;
    @Valid
    private Address homeAddress = new Address();
    @Valid
    private Address localAddress = new Address();
    /*
    * Województwo - область
    * Powiat - район
    * Gmina - город
    * Ulica
    * Dom
    * Mieszkania
    * Miejscowość-город
    * kod pocztowy
    * доп пункт место рождения
    * доп пункт место учебы
    * */

}

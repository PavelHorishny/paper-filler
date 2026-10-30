package app.paper_filler.web;

import java.time.LocalDate;

public class ClientForm {
    private Citizenship citizenship;
    private String name;
    private String surname;
    private String pesel;
    private String passport;
    private String account;
    private LocalDate fulfilmentDate;
    private LocalDate birthDate;
    private String phoneNumber;
    private String viber;
    private String email;
    private String taxOffice;
    @Valid
    private Address homeAddress = new Address();
    @Valid
    private Address localAddress = new Address();

}

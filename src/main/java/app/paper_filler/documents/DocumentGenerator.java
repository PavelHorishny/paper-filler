package app.paper_filler.documents;

import app.paper_filler.config.AppProperties;
import app.paper_filler.form.ClientForm;
import app.paper_filler.translit.TransliterationService;
import com.deepoove.poi.XWPFTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Service
@RequiredArgsConstructor
public class DocumentGenerator {

    private static final Pattern TAG = Pattern.compile("\\{\\{(\\w+)}}");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final AppProperties properties;
    private final TransliterationService translit;

    private static final DateTimeFormatter MONTH = DateTimeFormatter.ofPattern("MMMM", Locale.forLanguageTag("pl"));

    public byte[] generateZip(ClientForm form,List<String> docIds) throws IOException{
        Map<String,Object> data = buildData(form);

        ByteArrayOutputStream zipBytes = new ByteArrayOutputStream();
        try(ZipOutputStream zip = new ZipOutputStream(zipBytes)){
            for(DocumentDef doc : properties.documents()){
                if(!docIds.contains(doc.id())) continue;

                Path template = properties.templatesDir().resolve(doc.template());
                ByteArrayOutputStream docBytes = new ByteArrayOutputStream();
                try(XWPFTemplate tpl = XWPFTemplate.compile(template.toString()).render(data)){
                    tpl.write(docBytes);
                }

                String name = (doc.outputName() !=null ? fileName(doc.outputName(),data) : doc.id()) + ".docx";
                if (doc.company() != null) {
                    name = doc.company() + "/" + name;
                }
                zip.putNextEntry(new ZipEntry(name));
                zip.write(docBytes.toByteArray());
                zip.closeEntry();
            }
        }
        return zipBytes.toByteArray();
    }

    private String fileName(String pattern, Map<String, Object> data) {
        String name = TAG.matcher(pattern).replaceAll(m->
                Matcher.quoteReplacement(String.valueOf(data.getOrDefault(m.group(1),""))));
        return name.replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    private Map<String,Object> buildData(ClientForm form){
        Map<String, Object> data = new HashMap<>();/*
        data.put("name", form.getName());
        data.put("surname", form.getSurname());
        data.put("fulfilmentDate", formatDate(form.getFulfilmentDate()));
        data.put("location", "Szczecin");
        data.put("pesel", form.getPesel());
        data.put("homeAddress", form.getHomeAddress().shortAddress());
        data.put("localAddress", form.getLocalAddress().shortAddress());
        data.put("viber", text(form.getViber()));
        data.put("tel", text(form.getPhoneNumber()));
        data.put("email", text(form.getEmail()));
        data.put("birthDate", formatDate(form.getBirthDate()));
        data.put("birthPlace", text(form.getBirthPlace()));
        data.put("label", form.getCitizenship().getCountry());
        data.put("series", form.getPassport().getSeries());
        data.put("number", form.getPassport().getNumber());
        data.put("locRegion", form.getLocalAddress().getRegion());
        data.put("homeRegion", form.getHomeAddress().getRegion());
        data.put("locDistrict", form.getLocalAddress().getDistrict());
        data.put("homeDistrict", form.getHomeAddress().getDistrict());
        data.put("locCommune", form.getLocalAddress().getCommune());
        data.put("homeCommune", form.getHomeAddress().getCommune());
        data.put("locStreet", form.getLocalAddress().getStreet());
        data.put("homeStreet", form.getHomeAddress().getStreet());
        data.put("locHouse", form.getLocalAddress().getHouse());
        data.put("homeHouse", form.getHomeAddress().getHouse());
        data.put("locApartment", form.getLocalAddress().getApartment());
        data.put("homeApartment", form.getHomeAddress().getApartment());
        data.put("locPostal", form.getLocalAddress().getPostalCode());
        data.put("homePostal", form.getHomeAddress().getPostalCode());
        //data.put(getTag(TagShortening.LOCAL_CITY,form.getLocalAddress().getCity()), form.getLocalAddress().getCity());
        data.put("lCityLab",getTag(TagShortening.LOCAL_CITY,form.getLocalAddress().getCity()));
        data.put(TagShortening.LOCAL_CITY.tag,form.getLocalAddress().getCity());
        data.put("hCityLab",getTag(TagShortening.HOME_CITY,form.getHomeAddress().getCity()));
        data.put("homeCity", form.getHomeAddress().getCity());
        data.put("taxOffice", form.getTaxOffice() != null ? form.getTaxOffice().getAddress() : "");
        data.put("account", formatAccount(form.getAccount()));
        data.put("schoolName", text(form.getSchoolName()));
        data.put("month", form.getFulfilmentDate().format(MONTH));
        data.put("stanowisko", "magazynier");
        data.put("bank", text(form.getBank()));*/
        collectData(data,form);
        return data;
    }

    private static String text(String value){
        return value == null ? "" : value.strip();
    }

    private static String formatDate(LocalDate date){
        return date == null ? "": date.format(DATE);
    }

    private static String fullName(ClientForm form){
        return (text(form.getName()) + " " + text(form.getSurname())).strip();
    }
    private static String formatAccount(String digits) {
        if(digits == null || digits.length()<2) return text(digits);
        StringBuilder result = new StringBuilder(digits.substring(0,2));
        for(int i = 2; i < digits.length(); i+=4){
            result.append(' ').append(digits,i,Math.min(i+4,digits.length()));
        }
        return result.toString();
    }

    private String getTag(TagShortening tags, String name){
        if(name.length()+tags.full.length()<=tags.maxLength){
            return tags.full;
        }else{
            if(name.length()+tags.shorter.length()<=tags.maxLength){
                return tags.shorter;
            }else {
                return tags.shortest;
            }
        }
    }

    private void collectData(Map<String, Object> data, ClientForm form){
        for(Tags tag : Tags.values()){
            switch (tag){
                case NAME -> data.put(Tags.NAME.value, form.getName());
                case SURNAME -> data.put(Tags.SURNAME.value, form.getSurname());
                case FULFILMENT_DATE -> data.put(Tags.FULFILMENT_DATE.value, formatDate(form.getFulfilmentDate()));
                case LOCATION -> data.put(Tags.LOCATION.value, "Szczecin");
                case PESEL -> data.put(Tags.PESEL.value, form.getPesel());
                case HOME_ADDRESS -> data.put(Tags.HOME_ADDRESS.value, form.getHomeAddress().shortAddress());
                case LOCAL_ADDRESS -> data.put(Tags.LOCAL_ADDRESS.value, form.getLocalAddress().shortAddress());
                case VIBER -> data.put(Tags.VIBER.value, text(form.getViber()));
                case PHONE -> data.put(Tags.PHONE.value, text(form.getPhoneNumber()));
                case MAIL -> data.put(Tags.MAIL.value, text(form.getEmail()));
                case BIRTH_DATE -> data.put(Tags.BIRTH_DATE.value, formatDate(form.getBirthDate()));
                case BIRTH_PLACE -> data.put(Tags.BIRTH_PLACE.value, text(form.getBirthPlace()));
                case CITIZENSHIP -> data.put(Tags.CITIZENSHIP.value, form.getCitizenship().getCountry());
                case PASSPORT_SERIES -> data.put(Tags.PASSPORT_SERIES.value, form.getPassport().getSeries());
                case PASSPORT_NUMBER -> data.put(Tags.PASSPORT_NUMBER.value, form.getPassport().getNumber());
                case LOCAL_REGION -> data.put(Tags.LOCAL_REGION.value, form.getLocalAddress().getRegion());
                case HOME_REGION -> data.put(Tags.HOME_REGION.value, form.getHomeAddress().getRegion());
                case LOCAL_DISTRICT -> data.put(Tags.LOCAL_DISTRICT.value, form.getLocalAddress().getDistrict());
                case HOME_DISTRICT -> data.put(Tags.HOME_DISTRICT.value, form.getHomeAddress().getDistrict());
                case LOCAL_COMMUNE -> data.put(Tags.LOCAL_COMMUNE.value, form.getLocalAddress().getCommune());
                case HOME_COMMUNE -> data.put(Tags.HOME_COMMUNE.value, form.getHomeAddress().getCommune());
                case LOCAL_STREET -> data.put(Tags.LOCAL_STREET.value, form.getLocalAddress().getStreet());
                case HOME_STREET -> data.put(Tags.HOME_STREET.value, form.getHomeAddress().getStreet());
                case LOCAL_HOUSE -> data.put(Tags.LOCAL_HOUSE.value, form.getLocalAddress().getHouse());
                case HOME_HOUSE -> data.put(Tags.HOME_HOUSE.value, form.getHomeAddress().getHouse());
                case LOCAL_APARTMENT -> data.put(Tags.LOCAL_APARTMENT.value, form.getLocalAddress().getApartment());
                case HOME_APARTMENT -> data.put(Tags.HOME_APARTMENT.value, form.getHomeAddress().getApartment());
                case LOCAL_POSTAL_CODE -> data.put(Tags.LOCAL_POSTAL_CODE.value, form.getLocalAddress().getPostalCode());
                case HOME_POSTAL_CODE -> data.put(Tags.HOME_POSTAL_CODE.value, form.getHomeAddress().getPostalCode());
                case LOCAL_CITY_LABEL -> data.put(Tags.LOCAL_CITY_LABEL.value, getTag(TagShortening.LOCAL_CITY,form.getLocalAddress().getCity()));
                case LOCAL_CITY -> data.put(TagShortening.LOCAL_CITY.tag,form.getLocalAddress().getCity());
                case HOME_CITY_LABEL -> data.put(Tags.HOME_CITY_LABEL.value,getTag(TagShortening.HOME_CITY,form.getHomeAddress().getCity()));
                case HOME_CITY -> data.put(Tags.HOME_CITY.value, form.getHomeAddress().getCity());
                case TAX_OFFICE -> data.put(Tags.TAX_OFFICE.value, form.getTaxOffice() != null ? form.getTaxOffice().getAddress() : "");
                case BANK_ACCOUNT -> data.put(Tags.BANK_ACCOUNT.value, formatAccount(form.getAccount()));
                case SCHOOL_NAME -> data.put(Tags.SCHOOL_NAME.value, text(form.getSchoolName()));
                case MONTH -> data.put(Tags.MONTH.value, form.getFulfilmentDate().format(MONTH));
                case ROLE -> data.put(Tags.ROLE.value, "magazynier");
                case BANK_NAME -> data.put(Tags.BANK_NAME.value, text(form.getBank()));
            }
        }
    }
}

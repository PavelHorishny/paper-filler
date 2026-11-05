package app.paper_filler.core.translit;

import app.paper_filler.web.Citizenship;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransliterationService {

    private final UkrainianTransliterator ukrainian;
    private final BelarusianTransliterator belarusian;

    public String transliterate(String text, Citizenship citizenship) {
        if(text == null || citizenship == null) return text;
        return switch (citizenship){
            case UA -> ukrainian.transliterate(text);
            case BY -> belarusian.transliterate(text);
            case PL,MD -> text;
        };
    }


}

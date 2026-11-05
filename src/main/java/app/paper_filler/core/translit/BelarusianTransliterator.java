package app.paper_filler.core.translit;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BelarusianTransliterator implements Transliterator{

    private static final Map<Character, String> LETTERS = Map.ofEntries(
            Map.entry('а', "a"), Map.entry('б', "b"), Map.entry('в', "v"),
            Map.entry('г', "h"), Map.entry('ґ', "g"), Map.entry('д', "d"),
            Map.entry('е', "e"), Map.entry('ё', "io"), Map.entry('ж', "zh"),
            Map.entry('з', "z"), Map.entry('і', "i"), Map.entry('и', "i"),
            Map.entry('й', "i"), Map.entry('к', "k"), Map.entry('л', "l"),
            Map.entry('м', "m"), Map.entry('н', "n"), Map.entry('о', "o"),
            Map.entry('п', "p"), Map.entry('р', "r"), Map.entry('с', "s"),
            Map.entry('т', "t"), Map.entry('у', "u"), Map.entry('ў', "u"),
            Map.entry('ф', "f"), Map.entry('х', "kh"), Map.entry('ц', "ts"),
            Map.entry('ч', "ch"), Map.entry('ш', "sh"), Map.entry('щ', "shch"),
            Map.entry('ы', "y"), Map.entry('э', "e"), Map.entry('ю', "iu"),
            Map.entry('я', "ia"));

    @Override
    public String transliterate(String text) {
        if (text == null) return "";
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            char lower = Character.toLowerCase(c);

            if (CaseUtils.isApostrophe(c) || lower == 'ь' || lower == 'ъ') continue;

            String latin = LETTERS.get(lower);
            if (latin == null) {
                out.append(c);
                continue;
            }
            out.append(CaseUtils.applyCase(latin, text, i));
        }
        return out.toString();
    }
}

package app.paper_filler.core.translit;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UkrainianTransliterator implements Transliterator{

    private static final Map<Character, String> LETTERS = Map.ofEntries(
            Map.entry('а', "a"), Map.entry('б', "b"), Map.entry('в', "v"),
            Map.entry('г', "h"), Map.entry('ґ', "g"), Map.entry('д', "d"),
            Map.entry('е', "e"), Map.entry('ж', "zh"), Map.entry('з', "z"),
            Map.entry('и', "y"), Map.entry('і', "i"), Map.entry('к', "k"),
            Map.entry('л', "l"), Map.entry('м', "m"), Map.entry('н', "n"),
            Map.entry('о', "o"), Map.entry('п', "p"), Map.entry('р', "r"),
            Map.entry('с', "s"), Map.entry('т', "t"), Map.entry('у', "u"),
            Map.entry('ф', "f"), Map.entry('х', "kh"), Map.entry('ц', "ts"),
            Map.entry('ч', "ch"), Map.entry('ш', "sh"), Map.entry('щ', "shch"));

    /** Буквы, которые пишутся по-разному: {в начале слова, в остальных местах}. */
    private static final Map<Character, String[]> BY_POSITION = Map.of(
            'є', new String[]{"ye", "ie"},
            'ї', new String[]{"yi", "i"},
            'й', new String[]{"y", "i"},
            'ю', new String[]{"yu", "iu"},
            'я', new String[]{"ya", "ia"});

    @Override
    public String transliterate(String text) {
        if (text == null) return "";
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            char lower = Character.toLowerCase(c);

            if (CaseUtils.isApostrophe(c) || lower == 'ь') continue;   // не передаются

            String latin;
            if (BY_POSITION.containsKey(lower)) {
                latin = BY_POSITION.get(lower)[isWordStart(text, i) ? 0 : 1];
            } else if (lower == 'г' && i > 0 && Character.toLowerCase(text.charAt(i - 1)) == 'з') {
                latin = "gh";                                           // «зг» → «zgh»
            } else if (LETTERS.containsKey(lower)) {
                latin = LETTERS.get(lower);
            } else {
                out.append(c);                                          // латиница, цифры, пробелы
                continue;
            }
            out.append(CaseUtils.applyCase(latin, text, i));
        }
        return out.toString();
    }

    private static boolean isWordStart(String text, int i) {
        if (i == 0) return true;
        char prev = text.charAt(i - 1);
        return !Character.isLetter(prev) && !CaseUtils.isApostrophe(prev);
    }
}

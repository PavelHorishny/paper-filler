package app.paper_filler.core.translit;

final class CaseUtils {
    private CaseUtils() {
    }

    static String applyCase(String latin, String text, int i){
        if(!Character.isUpperCase(text.charAt(i))){
            return latin;
        }
        boolean nextUpper = i + 1 < text.length() && Character.isUpperCase(text.charAt(i + 1));
        boolean prevUpper = i > 0 && Character.isUpperCase(text.charAt(i - 1));
        if(nextUpper || prevUpper){
            return latin.toUpperCase();
        }
        return Character.toUpperCase(latin.charAt(0)) + latin.substring(1);
    }

    static boolean isApostrophe(char c){
        return c == '\'' || c == '’' || c == 'ʼ' || c == '`';
    }
}

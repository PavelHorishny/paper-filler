package app.paper_filler.bank;

import app.paper_filler.config.AppProperties;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class TableBankFinderTest {
    private final TableBankFinder finder = new TableBankFinder(
            new AppProperties(null, List.of(), List.of(), Map.of("102", "PKO Bank Polski S.A."),null, List.of()));

    @Test
    void findsBankByCode() {
        assertThat(finder.findBank("61102010140000071219812874")).isEqualTo("PKO Bank Polski S.A.");
    }

    @Test
    void ignoresSpaces() {
        assertThat(finder.findBank("61 1020 1014 0000 0712 1981 2874")).isEqualTo("PKO Bank Polski S.A.");
    }

    @Test
    void unknownCodeOrWrongLengthGivesEmpty() {
        assertThat(finder.findBank("61999010140000071219812874")).isEmpty();   // кода нет в таблице
        assertThat(finder.findBank("6110201014")).isEmpty();                   // не 26 цифр
        assertThat(finder.findBank(null)).isEmpty();
    }

}

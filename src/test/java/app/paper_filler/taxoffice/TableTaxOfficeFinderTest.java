package app.paper_filler.taxoffice;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class TableTaxOfficeFinderTest {

    private final TableTaxOfficeFinder finder =
            new TableTaxOfficeFinder(Path.of("config/teryt/szczecin-tax-offices.csv"));

    TableTaxOfficeFinderTest() throws Exception {
    }

    @Test
    void wholeStreet() {
        assertThat(finder.find("11926", null)).isEqualTo(TaxOffice.FIRST);        // 1 Maja
    }

    @Test
    void splitStreetByRange() {
        assertThat(finder.find("07029", "10")).isEqualTo(TaxOffice.FIRST);       // Jagiellońska 1-16
        assertThat(finder.find("07029", "74")).isEqualTo(TaxOffice.SECOND);      // 17-84
        assertThat(finder.find("07029", "90")).isEqualTo(TaxOffice.FIRST);       // 85-
        assertThat(finder.find("07029", "16A")).isEqualTo(TaxOffice.FIRST);
    }

    @Test
    void evenOnlyAndOthers() {
        assertThat(finder.find("29472", "30")).isEqualTo(TaxOffice.FIRST);       // Druckiego-Lubeckiego 28-44 чётные
        assertThat(finder.find("29472", "31")).isNull();                         // нечётный — не в списке
        assertThat(finder.find("04337", "16")).isEqualTo(TaxOffice.THIRD);       // Dubois 15-17
        assertThat(finder.find("04337", "18")).isEqualTo(TaxOffice.FIRST);       // Dubois «остальные»
    }

    @Test
    void unknown() {
        assertThat(finder.find("99999", "1")).isNull();                          // нет такой улицы
        assertThat(finder.find("07029", null)).isNull();                         // поделена, а номера нет
        assertThat(finder.find("29986", "5")).isNull();                          // Nabrzeże Wieleckie — «?»
    }
}

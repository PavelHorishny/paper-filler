package app.paper_filler;

import app.paper_filler.config.AppProperties;
import app.paper_filler.config.DocumentDef;
import app.paper_filler.config.FieldDef;
import app.paper_filler.config.FieldType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
class ConfigSmokeTest {

    @Autowired
    AppProperties props;

    @Test
    void configIsLoaded() {
        assertThat(props.fields()).hasSize(15);
        assertThat(props.documents())
                .extracting(DocumentDef::id)
                .containsExactly("umowa", "oswiadczenie", "aneks");
    }

    @Test
    void defaultsAndEnumsAreBound() {
        Map<String, FieldDef> byId = props.fields().stream()
                .collect(Collectors.toMap(FieldDef::id, f -> f));
        assertThat(byId.get("nr_paszportu").cache()).isFalse();
        assertThat(byId.get("adres").cache()).isTrue();          // сработал @DefaultValue
        assertThat(byId.get("data_urodzenia").type()).isEqualTo(FieldType.DATE);
        assertThat(byId.get("imie_nazwisko").translitTo()).isEqualTo("imie_nazwisko_lat");
    }
}

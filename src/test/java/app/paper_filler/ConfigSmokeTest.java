package app.paper_filler;

import app.paper_filler.config.AppProperties;
import app.paper_filler.documents.DocumentDef;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;



import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
class ConfigSmokeTest {

    @Autowired
    AppProperties props;

    @Test
    void documentsAreLoaded() {
        assertThat(props.documents()).isNotEmpty();
        assertThat(props.documents())
                .extracting(DocumentDef::id)
                .contains("Anketa");
    }
}

package app.paper_filler;

import app.paper_filler.config.AppProperties;
import app.paper_filler.documents.DocumentDef;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;


import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "app.templates-dir=src/test/resources/test-templates",
        "app.documents[0].id=Test",
        "app.documents[0].template=test.docx",
        "app.documents[0].output-name=test",
        "app.documents[0].required=true"
})
class ConfigSmokeTest {

    @Autowired
    AppProperties props;

    @Test
    void documentsAreLoaded() {
        assertThat(props.documents())
                .extracting(DocumentDef::id)
                .containsExactly("Test");
    }
}

package app.paper_filler.config;

import app.paper_filler.documents.DocumentDef;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "app")
public record AppProperties(
        Path templatesDir,
        List<DocumentDef> documents,
        List<String> allowedEmails,
        Map<String,String> banks,
        Release release
) {
    public record Release(String version, List<String> notes){}
}

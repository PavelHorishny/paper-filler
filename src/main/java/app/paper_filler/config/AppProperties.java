package app.paper_filler.config;

import app.paper_filler.documents.DocumentDef;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;
import java.util.List;

@ConfigurationProperties(prefix = "app")
public record AppProperties(
        Path templatesDir,
        List<DocumentDef> documents,
        List<String> allowedEmails
) {
}

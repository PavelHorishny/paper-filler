package app.paper_filler.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;
import java.util.List;

@ConfigurationProperties(prefix = "app")
public record AppProperties(
        Path templatesDir, Path outputDir, Path cacheDir,
        List<FieldDef> fields, List<DocumentDef> documents
) {
}

package app.paper_filler.config;

import app.paper_filler.documents.DocumentDef;
import app.paper_filler.form.CompanyAddress;
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
        Release release,
        List<CompanyAddress> companyAddresses
) {
    public AppProperties {
        if (documents == null) documents = List.of();
    }

    public record Release(String version, List<String> notes){}
}

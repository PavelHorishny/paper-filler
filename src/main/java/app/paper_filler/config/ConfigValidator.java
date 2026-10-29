package app.paper_filler.config;




import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



@Component
public class ConfigValidator implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(ConfigValidator.class);
    private final AppProperties props;

    ConfigValidator(AppProperties props) {
        this.props = props;
    }

    @Override
    public void run(ApplicationArguments args) {
        List<String> errors = new ArrayList<>();

        Set<String> fieldIds = new HashSet<>();
        for (FieldDef f : props.fields()) {
            if (!fieldIds.add(f.id())) errors.add("Повтор поля: " + f.id());
        }
        for (FieldDef f : props.fields()) {
            if (f.translitTo() != null && !fieldIds.contains(f.translitTo())) {
                errors.add("Поле " + f.id() + ": translit-to ведёт на неизвестное поле " + f.translitTo());
            }
        }

        Set<String> docIds = new HashSet<>();
        for (DocumentDef d : props.documents()) {
            if (!docIds.add(d.id())) errors.add("Повтор документа: " + d.id());
            Path tpl = props.templatesDir().resolve(d.template());
            if (!Files.isRegularFile(tpl)) {
                errors.add("Документ " + d.id() + ": нет файла " + tpl.toAbsolutePath());
            }
        }

        if (!errors.isEmpty()) {
            throw new IllegalStateException("Ошибки конфигурации:\n  " + String.join("\n  ", errors));
        }
        long required = props.documents().stream().filter(DocumentDef::required).count();
        log.info("Loaded {} fields, {} documents ({} required)");
    }
}

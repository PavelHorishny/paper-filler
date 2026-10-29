package app.paper_filler.config;

import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;

public record FieldDef (String id, String label, FieldType type, boolean required,
                        String translitTo, String pattern, List<String> options,
                        @DefaultValue("true") boolean cache) { }

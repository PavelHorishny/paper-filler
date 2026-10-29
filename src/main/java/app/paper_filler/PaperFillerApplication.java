package app.paper_filler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class PaperFillerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaperFillerApplication.class, args);
	}

}

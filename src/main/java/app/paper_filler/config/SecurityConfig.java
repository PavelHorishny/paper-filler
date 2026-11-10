package app.paper_filler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain security (HttpSecurity http, AppProperties props) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/error").permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(ui->ui.oidcUserService(allowlist(props.allowedEmails())))
                        /*.userInfoEndpoint(ui -> ui.oidcUserService(allowlist(props.allowedEmails()))))*/
                        .failureHandler(((request, response, exception) -> {
                            response.setContentType("text/plain;charset=UTF-8");
                            response.getWriter().write("failure: "+exception.getMessage());
                        })));
                /*.logout(logout -> logout.logoutSuccessUrl("/"));*/
        return http.build();
    }

    private OAuth2UserService<OidcUserRequest, OidcUser> allowlist(List <String> emails) {
        Set<String> allowed = emails == null ? Set.of() : emails.stream()
                .map(e-> e.strip().toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());
        OidcUserService google = new OidcUserService();

        return request -> {
            OidcUser user = google.loadUser(request);
            String email = user.getEmail() == null ? "":user.getEmail().toLowerCase(Locale.ROOT);
            if(!allowed.contains(email)){
                throw new OAuth2AuthenticationException(new OAuth2Error("access_denied"),"Нет доступа "+email);
            }
            return user;
        };
    }
}

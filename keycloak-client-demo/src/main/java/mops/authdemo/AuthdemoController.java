package mops.authdemo;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Random;

@Controller
public class AuthdemoController {

    private final Counter authenticatedAccess;
    private final Counter publicAccess;
    @Autowired
    RestTemplate serviceAccountRestTemplate;
    @Autowired
    private KeycloakRestTemplate template;

    public AuthdemoController(MeterRegistry registry) {
        authenticatedAccess = registry.counter("access.authenticated");
        publicAccess = registry.counter("access.public");
    }

    /**
     * Nimmt das Authentifizierungstoken von Keycloak und erzeugt ein AccountDTO für die Views.
     *
     * @param token
     * @return neuen Account der im Template verwendet wird
     */
    private Account createAccountFromPrincipal(KeycloakAuthenticationToken token) {
        KeycloakPrincipal principal = (KeycloakPrincipal) token.getPrincipal();
        return new Account(
                principal.getName(),
                principal.getKeycloakSecurityContext().getIdToken().getEmail(),
                null,
                token.getAccount().getRoles());
    }

    @GetMapping("/")
    @Secured({"ROLE_studentin", "ROLE_orga"})
    public String index(KeycloakAuthenticationToken token, Model model) {

        /**
         * THIS IS JUST AN EXAMPLE! DO NOT QUERY A SERVICE IN THE REQUEST/RESPONSE CYCLE!
         */
        var res = Arrays.asList(serviceAccountRestTemplate.getForEntity("http://localhost:8080/api/text", Entry[].class).getBody());

        model.addAttribute("account", createAccountFromPrincipal(token));
        model.addAttribute("rest_account_name", res.get(0).getAttribute2());
        model.addAttribute("entries", res.subList(1, res.size()));
        return "index";
    }

    @GetMapping("/id")
    @Secured({"ROLE_studentin", "ROLE_orga"})
    public String withId(KeycloakAuthenticationToken token, Model model) {
        int randomInt = new Random().nextInt(10);

        /**
         * THIS IS JUST AN EXAMPLE! DO NOT QUERY A SERVICE IN THE REQUEST/RESPONSE CYCLE!
         */
        var res = Arrays.asList(serviceAccountRestTemplate.getForEntity("http://localhost:8080/api/specific-text/" + randomInt, Entry[].class).getBody());

        model.addAttribute("account", createAccountFromPrincipal(token));
        model.addAttribute("rest_account_name", res.get(0).getAttribute2());
        model.addAttribute("id", randomInt);
        model.addAttribute("entries", res.subList(1, res.size()));
        return "id";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) throws Exception {
        request.logout();
        return "redirect:/";
    }
}

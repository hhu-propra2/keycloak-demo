package mops.authdemo;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class AuthdemoController {

    KeycloakService keycloakService;

    private final Counter authenticatedAccess;
    private final Counter publicAccess;

    public AuthdemoController(MeterRegistry registry, KeycloakService service) {
        authenticatedAccess = registry.counter("access.authenticated");
        publicAccess = registry.counter("access.public");
        this.keycloakService = service;
    }

    @GetMapping("/")
    public String index(KeycloakAuthenticationToken token, Model model) {
        if (token != null) {
            model.addAttribute("account", keycloakService.createAccountFromPrincipal(token));
        }
        publicAccess.increment();
        return "index";
    }

    @GetMapping("/orga")
    @Secured("ROLE_orga")
    public String orga(KeycloakAuthenticationToken token, Model model) {
        model.addAttribute("account", keycloakService.createAccountFromPrincipal(token));
        model.addAttribute("entries", Entry.generate(10));
        authenticatedAccess.increment();
        return "orga";
    }

    @GetMapping("/studi")
    @Secured("ROLE_studentin")
    public String studentin(KeycloakAuthenticationToken token, Model model) {
        model.addAttribute("account", keycloakService.createAccountFromPrincipal(token));
        model.addAttribute("entries", Entry.generate(10));
        authenticatedAccess.increment();
        return "studentin";
    }


    @GetMapping("/personal")
    @Secured({"ROLE_studentin","ROLE_orga"})
    public String personal(KeycloakAuthenticationToken token, Model model) {
        model.addAttribute("account", keycloakService.createAccountFromPrincipal(token));
        authenticatedAccess.increment();
        return "personal";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) throws Exception {
        request.logout();
        return "redirect:/";
    }
}

package mops.authdemo;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.security.RolesAllowed;

@Controller
public class MopsigerController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    // Rollenberechtigung eingerichtet in SecurityConfig
    @GetMapping("/my-route")
    public String myRoute(KeycloakAuthenticationToken token, Model model) {
        KeycloakPrincipal principal = (KeycloakPrincipal) token.getPrincipal();
        model.addAttribute("username", principal.getName());
        model.addAttribute("email", principal.getKeycloakSecurityContext().getIdToken().getEmail());
        model.addAttribute("entries", Entry.generate(10));
        return "orga";
    }

    @GetMapping("/orga")
    @Secured("ROLE_orga")
    public String orga(KeycloakAuthenticationToken token, Model model) {
        KeycloakPrincipal principal = (KeycloakPrincipal) token.getPrincipal();
        model.addAttribute("username", principal.getName());
        model.addAttribute("role", "OrganisatorIn");
        model.addAttribute("email", principal.getKeycloakSecurityContext().getIdToken().getEmail());
        model.addAttribute("entries", Entry.generate(10));
        return "orga";
    }

    @GetMapping("/studi")
    @Secured("ROLE_studentin")
    public String studentin(KeycloakAuthenticationToken token, Model model) {
        KeycloakPrincipal principal = (KeycloakPrincipal) token.getPrincipal();
        model.addAttribute("username", principal.getName());
        model.addAttribute("email", principal.getKeycloakSecurityContext().getIdToken().getEmail());
        model.addAttribute("entries", Entry.generate(10));
        return "studentin";
    }

    @GetMapping("/korrektorin")
    @RolesAllowed("ROLE_korrektorin")
    public String korrektorin(KeycloakAuthenticationToken token, Model model) {
        KeycloakPrincipal principal = (KeycloakPrincipal) token.getPrincipal();
        model.addAttribute("username", principal.getName());
        model.addAttribute("email", principal.getKeycloakSecurityContext().getIdToken().getEmail());
        model.addAttribute("entries", Entry.generate(10));
        return "korrektorin";
    }

    @GetMapping("/mixed")
    @Secured({"ROLE_korrektorin", "ROLE_orga"})
    public String korrektorin_and_orga(KeycloakAuthenticationToken token, Model model) {
        KeycloakPrincipal principal = (KeycloakPrincipal) token.getPrincipal();
        model.addAttribute("username", principal.getName());
        model.addAttribute("role", "OrganisatorIn / KorrektorIn");
        model.addAttribute("email", principal.getKeycloakSecurityContext().getIdToken().getEmail());
        model.addAttribute("entries", Entry.generate(10));
        return "mixed";
    }

    @GetMapping("/personal")
    @RolesAllowed({"ROLE_korrektorin", "ROLE_orga", "ROLE_studentin"})
    public String personal(KeycloakAuthenticationToken token, Model model) {
        KeycloakPrincipal principal = (KeycloakPrincipal) token.getPrincipal();
        model.addAttribute("username", principal.getName());
        model.addAttribute("role", token.getAccount().getRoles().iterator().next());
        model.addAttribute("email", principal.getKeycloakSecurityContext().getIdToken().getEmail());

        Entry[] entries = new Entry[]{
            new Entry("username", principal.getName(), "principal.getName()"),
            new Entry("your roles", String.join(",", token.getAccount().getRoles()), "token.getAccount().getRoles()"),
            new Entry("email address", principal.getKeycloakSecurityContext().getIdToken().getEmail(), "principal.getKeycloakSecurityContext().getIdToken().getEmail()"),
            new Entry("matrikelnummer", "???", "???")};

        model.addAttribute("entries", entries);
        return "personal";
    }
}

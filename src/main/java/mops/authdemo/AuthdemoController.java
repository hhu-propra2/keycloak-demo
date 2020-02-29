package mops.authdemo;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;

@Controller
public class AuthdemoController {

    @GetMapping("/")
    public String index(KeycloakAuthenticationToken token, Model model) {
        if (token != null) {
            KeycloakPrincipal principal = (KeycloakPrincipal) token.getPrincipal();
            model.addAttribute("username", principal.getName());
        }
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

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) throws Exception {
        request.logout();
        return "redirect:/";
    }
}

package mops.authdemo;

import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AuthDemoRestController {

    @GetMapping("text")
    @Secured("ROLE_keycloak_demo_api_user")
    public List<Entry> generateText(KeycloakAuthenticationToken token) {
        Entry userEntry = new Entry("request was send by user:",token.getName(),"");
        var generatedEntries = Entry.generate(10).stream().map(e -> new Entry("From Keycloak-Demo : " + e.getAttribute1(),
                "From Keycloak-Demo : " + e.getAttribute2(),
                "From Keycloak-Demo : " + e.getAttribute3()))
                .collect(Collectors.toList());
        generatedEntries.add(0,userEntry);
        return generatedEntries;
    }

    @GetMapping("specific-text/{some_id}")
    @Secured("ROLE_keycloak_demo_api_user")
    public List<Entry> generateTextWithId(KeycloakAuthenticationToken token, @PathVariable("some_id") Long id) {
        Entry userEntry = new Entry("request was send by user:",token.getName(),"");
        var generatedEntries = Entry.generate(10).stream().map(e -> new Entry("From Keycloak-Demo : " + id + " " + e.getAttribute1(),
                "From Keycloak-Demo : " + id + " " + e.getAttribute2(),
                "From Keycloak-Demo : " + id + " " + e.getAttribute3()))
                .collect(Collectors.toList());
        generatedEntries.add(0,userEntry);
        return generatedEntries;
    }
}

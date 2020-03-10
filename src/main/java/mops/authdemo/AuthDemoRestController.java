package mops.authdemo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AuthDemoRestController {

    @GetMapping("bla")
    public List<Entry> bla() {
        return Entry.generate(10).stream().map(e -> new Entry("From Keycloak-Demo : " + e.getAttribute1(),
                "From Keycloak-Demo : " + e.getAttribute2(),
                "From Keycloak-Demo : " + e.getAttribute3()))
                .collect(Collectors.toList());
    }
}

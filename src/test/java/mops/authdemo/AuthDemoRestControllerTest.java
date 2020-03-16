package mops.authdemo;

import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakSecurityComponents;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashSet;
import java.util.Set;

import static mops.authdemo.KeycloakTokenMock.setupTokenMock;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@ComponentScan(basePackageClasses = { KeycloakSecurityComponents.class, KeycloakSpringBootConfigResolver.class })
class AuthDemoRestControllerTest {
    @Autowired
    MockMvc mvc;

    @Autowired
    WebApplicationContext context;

    @MockBean(answer = Answers.RETURNS_DEEP_STUBS)
    MeterRegistry registry;

    @MockBean
    KeycloakService keycloakService;

    @InjectMocks
    AuthDemoRestController controller;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void testText() throws Exception {
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_keycloak_demo_api_user");
        Account account = new Account("name", "User@email.de","image", roles);
        setupTokenMock(account);

        mvc.perform(get("/api/text"))
                .andExpect(status().isOk());
    }

    @Test
    void testTextReturns() throws Exception {
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_keycloak_demo_api_user");
        Account account = new Account("IchBinEinUser", "User@email.de","image", roles);
        setupTokenMock(account);

        var result = mvc.perform(get("/api/text"))
                .andExpect(status().isOk())
                .andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("IchBinEinUser"));
    }

}
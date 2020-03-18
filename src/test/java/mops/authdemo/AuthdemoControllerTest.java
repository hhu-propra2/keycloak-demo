package mops.authdemo;

import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakSecurityComponents;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashSet;
import java.util.Set;

import static mops.authdemo.KeycloakTokenMock.setupTokenMock;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@WebMvcTest
@AutoConfigureMockMvc
@ComponentScan(basePackageClasses = { KeycloakSecurityComponents.class, KeycloakSpringBootConfigResolver.class })
class AuthdemoControllerTest {
    @Autowired
    MockMvc mvc;

    @Autowired
    WebApplicationContext context;

    @MockBean(answer = Answers.RETURNS_DEEP_STUBS)
    MeterRegistry registry;

    @MockBean
    KeycloakService keycloakService;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void testStudiView() throws Exception {
        Set<String> roles = new HashSet<>();
        roles.add("studentin");
        Account account = new Account("name", "User@email.de","image", roles);
        setupTokenMock(account);
        when(keycloakService.createAccountFromPrincipal(any(KeycloakAuthenticationToken.class))).thenReturn(account);

        mvc.perform(get("/studi"))
                .andExpect(status().isOk())
                .andExpect(view().name("studentin"));
    }

    @Test
    void testStudiModel() throws Exception {
        Set<String> roles = new HashSet<>();
        roles.add("studentin");
        Account account = new Account("name", "User@email.de","image", roles);
        setupTokenMock(account);
        when(keycloakService.createAccountFromPrincipal(any(KeycloakAuthenticationToken.class))).thenReturn(account);

        mvc.perform(get("/studi"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attribute("account", account));
    }
}